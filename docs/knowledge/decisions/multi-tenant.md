# Multi-tenant / Organizações (decisão futura)

Tags: #decisao #futuro #arquitetura #multi-tenant

Status: 📋 Planejada — implementação adiada até o sistema estar validado em uso real

---

## Contexto

Durante o planejamento da Fase 6 (Eventos), surgiu a ideia de o sistema servir **várias instituições diferentes** ao mesmo tempo, cada uma com login, produtos, eventos e vendas isolados das demais — a conta de uma instituição nunca vê nem interfere na de outra.

## Decisão

**Adiar** a implementação até o sistema atual (uma instituição só) estar validado em eventos reais. Documentar o plano agora para não perder o raciocínio, mas não misturar com a Fase 6.

## Por que adiar

- Multi-tenant muda módulos que já estão prontos e testados (`Produto`, `Evento`, `Venda`) — fazer isso junto com Eventos aumenta a chance de quebrar algo sem perceber
- Já existem dados reais em produção — migrar para debaixo de uma organização exige cuidado e validação, melhor feito isoladamente
- Regra geral do projeto: um módulo de cada vez

## O que multi-tenant exige (quando chegar a hora)

1. Nova entidade `Organizacao`
2. `Usuario`, `Produto`, `Evento` ganham `organizacaoId`
3. A regra "1 evento aberto por vez" (Fase 6) passa a ser por organização, não global
4. Toda consulta (Produto, Evento, Venda) passa a filtrar pela organização do usuário logado — não é um lugar só, é um cuidado que se repete em cada service/repository
5. Proteção contra vazamento entre contas (IDOR): toda busca por ID confere se o recurso pertence à organização de quem pede; se não pertence, **404** (nunca 403 — não confirma que o recurso existe em outra conta)
6. Novo endpoint público `POST /organizacoes` — cria uma organização nova + seu primeiro ADMIN (hoje `/auth/register` exige um ADMIN pré-existente, o que trava uma instituição nova sem nenhum usuário ainda)
7. Migração dos dados existentes em produção para uma organização "padrão/legado"

## Escala e custo

- Dimensionado para **dezenas de acessos simultâneos** (20-100), não milhares
- **Zero custo** — é decisão de modelagem, não de infraestrutura. Render + Neon free tier comportam esse volume mesmo com várias instituições pequenas
- Se um dia crescer o bastante para precisar de plano pago, a troca é só configuração (variáveis de ambiente + Dockerfile já preparados desde a Fase 4) — sem reescrever código

## Relacionado
- [[fase-5-vendas]]
- [[arquitetura]]
- [[deploy]]
