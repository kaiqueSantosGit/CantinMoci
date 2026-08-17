# CantinMoci — Knowledge Graph

> Rede de conhecimento viva do projeto. Atualizada pelos agentes a cada sessão.
> Leia este arquivo primeiro para entender o estado atual do projeto.

---

## Entidades do sistema
- [[Produto]] — cadastro e controle de estoque
- [[Venda]] — registro de vendas e itens
- [[ItemVenda]] — relação entre venda e produtos
- [[Usuario]] — autenticação e permissões
- [[Evento]] — agrupamento de vendas por evento

---

## Módulos implementados
- [[fase-1-setup]] ✅ — ambiente e endpoint /health
- [[fase-2-produto]] ✅ — CRUD completo de Produto (10 testes Postman validados)
- [[fase-3-auth]] ✅ — autenticação JWT completa: login, cadastro (restrito a ADMIN), 401 correto, testada em produção
- [[fase-4-deploy]] ✅ — no ar em https://cantinmoci.onrender.com (Render + Neon)

## Módulos futuros
- [[fase-5-vendas]] ⏳ — sistema de vendas, próxima etapa (atenção: concorrência no estoque)
- [[fase-6-eventos]] — gestão de eventos
- [[fase-7-usuarios]] — trocar senha, listar/desativar usuário, admin resetar senha
- [[fase-8-frontend]] — interface web (requer configurar CORS)

## Backlog (pós-lançamento real)
- [[backlog]] — melhorias mapeadas mas não bloqueantes: rate limiting, revogação de token, auditoria, paginação, backup, monitoramento

---

## Decisões técnicas
- [[stack]] — por que Java 21, Spring Boot, PostgreSQL
- [[arquitetura]] — estrutura em camadas do backend
- [[deploy]] — por que Render + Neon, sem custo

## Conceitos aprendidos
- [[JPA]] — mapeamento objeto-relacional
- [[Spring-Security]] — autenticação e autorização

---

## Agentes e responsabilidades
| Agente | Lê | Escreve |
|---|---|---|
| mentor | index, learned/ | learned/ |
| backend-dev | index, entities/, decisions/ | entities/, modules/ |
| db-agent | index, entities/ | entities/, decisions/ |
| qa-agent | index, modules/ | modules/ |
| code-reviewer | index, entities/, modules/ | modules/ |
| ui-designer | index, entities/ | modules/ |
| frontend-dev | index, entities/, modules/ | modules/ |