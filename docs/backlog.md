# Backlog de Melhorias Futuras

> Mapeado em 2026-08-17, numa varredura de lacunas em todo o projeto (Produto,
> Auth, Deploy), pensando em robustez para produção real. Nenhum item aqui
> bloqueia as próximas fases (Vendas, Eventos, Gestão de Usuários, Frontend)
> — a ideia é revisar esta lista **depois** do sistema validado com uso real
> em pelo menos um evento beneficente, e decidir o que vale a pena priorizar
> a partir da experiência real de uso.

---

## Segurança

### Rate limiting / proteção contra força bruta no login
Hoje `POST /auth/login` aceita tentativas ilimitadas. Alguém poderia tentar
adivinhar a senha de um usuário sem nenhum bloqueio. Para um sistema pequeno,
de time fechado, o risco é baixo — mas é o tipo de proteção padrão em
qualquer API de autenticação real.
**Quando priorizar:** se o sistema ficar acessível publicamente por muito
tempo (não só durante eventos).

### Revogação de token JWT (logout de verdade)
Hoje um token JWT é válido até expirar sozinho (24h) — não existe um jeito de
"invalidar" um token antes disso (ex: se o celular de um operador for
roubado). Implementar isso exige guardar estado (lista de tokens revogados
ou trocar para refresh tokens), o que perde um pouco a simplicidade do JWT
stateless que temos hoje.
**Quando priorizar:** se algum incidente real acontecer, ou se o time
crescer e dispositivos compartilhados/perdidos virarem uma preocupação.

### Política de senha mais forte
`RegisterRequestDTO` hoje só exige mínimo de 6 caracteres. Dá pra evoluir
para exigir letra maiúscula, número, etc.
**Quando priorizar:** baixa prioridade para um time pequeno e de confiança;
considerar se o número de usuários crescer.

---

## Dados e Operação

### Auditoria (log de quem fez o quê)
Quando o módulo de Vendas existir, vai ser importante saber **quem**
registrou cada venda ou alterou um produto — não só para prestação de contas
do evento beneficente, mas para investigar problemas.
**Quando priorizar:** vale considerar já durante a modelagem da Fase 5
(Vendas), mesmo que a implementação completa fique para depois — pelo menos
guardar o `usuario_id` de quem fez a venda já ajuda bastante.

### Paginação nas listagens
`GET /produtos` (e futuramente `/vendas`, `/usuarios`) devolvem a lista
inteira de uma vez. Para o volume de um evento pequeno (dezenas de produtos)
isso não é problema — mas não escala indefinidamente.
**Quando priorizar:** se o catálogo de produtos ou o histórico de vendas
crescer muito (múltiplos eventos acumulados no mesmo banco).

### Estratégia de backup do banco
O plano gratuito do Neon tem retenção de backup limitada (poucos dias). Se
os dados de um evento (vendas, produtos) forem perdidos, hoje não temos um
plano B.
**Quando priorizar:** antes do primeiro evento real com dados que importam
de verdade — vale pelo menos um export manual (`pg_dump`) antes e depois de
cada evento, como prática simples enquanto não automatizamos nada.

---

## Qualidade de Código

### Tratamento de erro padronizado (`@ControllerAdvice` global)
Hoje cada erro tem sua própria exceção com `@ResponseStatus`
(`ResourceNotFoundException`, `UnauthorizedException`,
`EmailJaCadastradoException`). Funciona bem no tamanho atual do projeto, mas
um `@ControllerAdvice` central padronizaria o formato do corpo de erro em
todas as rotas (hoje o formato varia um pouco dependendo de onde o erro é
lançado).
**Quando priorizar:** quando o número de exceções customizadas crescer o
suficiente para a repetição incomodar, ou ao construir o frontend (que se
beneficia de um formato de erro 100% previsível).

---

## Infraestrutura

### Monitoramento / alerta de uptime
Não existe hoje nenhum aviso automático se o backend cair. Ferramentas
gratuitas como o UptimeRobot conseguem checar `/health` periodicamente e
avisar por email/WhatsApp se parar de responder.
**Quando priorizar:** antes de depender do sistema durante um evento ao
vivo — um alerta simples já ajuda bastante a reagir rápido.

### CORS (Fase 8 — Frontend)
Não é bem "backlog", já está mapeado como item obrigatório da Fase 8 no
[DEV_LOG.md](../DEV_LOG.md) — mencionado aqui só para lembrar que o
frontend não vai conseguir chamar a API sem essa configuração.

---

## Notas operacionais (não são código)

### Checklist do dia do evento
O plano gratuito do Render "dorme" a aplicação após ~15 minutos sem uso, e a
primeira requisição depois disso demora ~30-50s para acordar. Antes de abrir
a cantina no evento, vale a pena "acordar" o servidor de propósito:
```
curl https://cantinmoci.onrender.com/health
```
Alguns minutos antes de começar a usar o sistema de verdade, para que a
primeira venda do dia não sofra esse atraso.
