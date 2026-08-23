# CantinMoci — Dev Log

> Arquivo de contexto de desenvolvimento. Atualizado a cada etapa concluída.
> Sempre leia este arquivo no início de uma nova sessão para entender onde o projeto está.

---

## Stack do Projeto

| Camada | Tecnologia | Versão |
|---|---|---|
| Linguagem | Java | 21 LTS (Oracle JDK) |
| Framework | Spring Boot | 3.5.14 |
| Banco de Dados | PostgreSQL | 17.10 |
| Build | Maven | 3.9.16 |
| Visual DB | DBeaver | 26.0.5 (Community) |
| Versionamento | Git + GitHub | Git 2.51.2 |
| API Testing | Postman | (instalado e em uso) |
| Frontend | A definir | Candidatos: HTML/CSS/JS ou React |

---

## Ambiente de Desenvolvimento

- **OS:** Windows 11
- **Java HOME:** `C:\Program Files\Java\jdk-21.0.10`
- **Maven:** `%USERPROFILE%\dev-tools\maven\bin` (adicionado ao PATH do usuário)
- **PostgreSQL porta:** 5432
- **PostgreSQL usuário:** postgres (senha definida pelo usuário)
- **PostgreSQL serviço Windows:** `postgresql-x64-17`

---

## Estrutura de Pastas (planejada)

```
cantinmoci/
├── backend/          ← projeto Spring Boot ficará aqui
├── frontend/         ← a definir
├── docs/             ← documentação técnica futura
├── database/         ← scripts SQL futuros
├── context.md        ← visão geral e regras do projeto
└── DEV_LOG.md        ← este arquivo (contexto de progresso)
```

---

## Fluxo Obrigatório por Funcionalidade

Cada funcionalidade segue este fluxo antes de avançar:

```
1. Planejamento → 2. Modelagem → 3. Backend → 4. Banco de Dados
→ 5. Testes (Postman) → 6. Frontend → 7. Revisão → 8. Documentação
```

---

## Fases e Status

### Fase 0 — Ambiente ✅ CONCLUÍDA

- [x] Java 21 LTS verificado
- [x] Git instalado e verificado
- [x] Maven 3.9.16 instalado e configurado no PATH
- [x] PostgreSQL 17 instalado, serviço rodando na porta 5432
- [x] Senha do usuário postgres definida
- [x] DBeaver Community 26.0.5 instalado

---

### Fase 1 — Setup do Projeto Spring Boot ✅ CONCLUÍDA

**Objetivo:** Criar a estrutura base do backend, conectar ao banco e subir a primeira rota.

**Etapas:**
- [x] Criar projeto via Spring Initializr (start.spring.io)
- [x] Entender a estrutura de arquivos gerada
- [x] Configurar `application.properties` (conexão com PostgreSQL)
- [x] Criar banco de dados `cantinmoci` no PostgreSQL
- [x] Criar endpoint de teste `GET /health`
- [x] Testar no Postman
- [x] Primeiro commit no Git

**Dependências Spring Boot adicionadas:**
- Spring Web — para criar APIs REST
- Spring Data JPA — para comunicação com o banco via código Java
- PostgreSQL Driver — driver de conexão com o PostgreSQL
- Spring Boot DevTools — reinício automático do servidor em desenvolvimento

---

### Fase 2 — Módulo Produto (CRUD) ✅ CONCLUÍDA

**Objetivo:** Criar o CRUD completo de produtos com banco de dados.

**Etapas:**
- [x] Modelar entidade `Produto`
- [x] Criar tabela no PostgreSQL (via ddl-auto=update)
- [x] Criar camadas: Model → Repository → Service → Controller → DTO
- [x] Implementar endpoints REST
- [x] Roteiro de testes manuais gerado — `docs/qa/test-produto.md`
- [x] Executar 10 testes no Postman — todos passando
- [x] Documentar

**Endpoints implementados:**
| Método | Rota | Descrição | Status esperado |
|---|---|---|---|
| GET | /produtos | Listar todos os ativos | 200 OK |
| GET | /produtos/{id} | Buscar por ID | 200 OK / 404 |
| POST | /produtos | Criar produto | 201 Created / 400 |
| PUT | /produtos/{id} | Atualizar produto | 200 OK / 404 |
| DELETE | /produtos/{id} | Soft delete | 204 No Content / 404 |

---

### Fase 3 — Módulo Autenticação ✅ CONCLUÍDA (fechada em 2026-08-17)

- [x] Enum Cargo (ADMIN, OPERADOR)
- [x] Entidade Usuario (id, nome, email, senha hash BCrypt, cargo) — implementa UserDetails
- [x] UsuarioRepository — findByEmail(String), existsByEmail(String)
- [x] DTOs: LoginRequestDTO, TokenResponseDTO, RegisterRequestDTO, UsuarioResponseDTO
- [x] JwtService — gerar/extrair/validar token HS256 com expiração 24h
- [x] UserDetailsServiceImpl — loadUserByUsername por email
- [x] AuthService — autenticar com BCrypt + gerar token + cadastrar usuário
- [x] JwtAuthFilter — intercepta header Authorization: Bearer
- [x] SecurityConfig — rotas públicas: /health, /auth/login, /error; /auth/register exige ADMIN; demais autenticadas; STATELESS
- [x] AuthController — POST /auth/login, POST /auth/register
- [x] Dependências JWT e Spring Security adicionadas ao pom.xml
- [x] Tabela `usuarios` criada automaticamente pelo Hibernate
- [x] Validado em produção (curl): login retorna token, `/produtos` aceita o token e recusa sem ele
- [x] Login inválido corrigido: retornava 500, agora retorna 401 (`UnauthorizedException`)
- [x] Endpoint de cadastro criado (`POST /auth/register`, restrito a ADMIN) — não depende mais de INSERT manual no banco
- [x] Roteiro formal `docs/qa/test-auth.md` (padrão do `test-produto.md`) — 11 testes

**Endpoints implementados:**
| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | /auth/login | Recebe email+senha, retorna token JWT | Pública |
| POST | /auth/register | Cadastra novo usuário | Exige token de ADMIN |

**Rotas protegidas (exigem Bearer token):**
| Método | Rota |
|---|---|
| GET | /produtos |
| GET | /produtos/{id} |
| POST | /produtos |
| PUT | /produtos/{id} |
| DELETE | /produtos/{id} |
| POST | /auth/register (exige especificamente ADMIN) |

**Bug encontrado durante a correção do 401 → 403:** o Spring Security também filtra o redirecionamento interno do Spring para `/error` (usado para montar o corpo JSON de qualquer resposta de erro). Sem liberar essa rota, o segundo filtro sobrescrevia qualquer status de erro (401, 404...) para 403. Corrigido liberando `/error` como rota pública.

---

### Fase 4 — Deploy Gratuito (Infraestrutura) ✅ CONCLUÍDA (2026-08-17)

**Objetivo:** deixar o backend acessível publicamente pela internet, sem custo e sem depender de uma máquina pessoal ligada. Alcançado.

**Arquitetura em produção:**
- Backend → **Render** (free tier, via Docker) — **https://cantinmoci.onrender.com**
- Banco de Dados → **Neon** (PostgreSQL gerenciado, free tier, região `us-east-2`)
- Sem domínio próprio — subdomínio gratuito do Render
- Guia completo: [`docs/deploy.md`](docs/deploy.md)

**Preparação do código:**
- [x] Externalizar configuração sensível para variáveis de ambiente com fallback local
- [x] `application.properties` versionado (sem segredo nenhum); segredos locais isolados em `application-local.properties` (fora do Git, importado via `spring.config.import`)
- [x] `server.port` lendo da variável `PORT` (exigida pelo Render)
- [x] `Dockerfile` multi-stage (garante Java 21 em qualquer plataforma)
- [x] Guia passo a passo em `docs/deploy.md`

**Configuração na nuvem:**
- [x] Conta no Neon, banco PostgreSQL provisionado (conexão direta, sem pooler — evita conflito com o pool do HikariCP)
- [x] Conta no Render, repositório conectado
- [x] Web Service configurado (Docker, root directory `backend/cantinmoci`, região Oregon)
- [x] Variáveis de ambiente cadastradas: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`
- [x] Deploy rodando (`Live`)

**Bug encontrado e corrigido durante o deploy:**
- 1ª tentativa falhou (`Failed to configure a DataSource`) — `application.properties` estava no `.gitignore` e nunca chegava à imagem Docker do Render. Corrigido separando config versionada (`application.properties`) de segredos locais (`application-local.properties`, fora do Git). Ver commit `d93aa1f`.

**Validação:**
- [x] `GET /health` → 200 OK na URL pública
- [x] `GET /produtos` sem token → 403 Forbidden (segurança ativa)
- [x] `POST /auth/login` com usuário de teste (inserido manualmente via SQL no Neon) → 200 OK + token JWT
- [x] `GET /produtos` com token → 200 OK

---

### Fase 5 — Módulo Vendas + Estoque ✅ CONCLUÍDA (2026-08-17)

**Decisões de negócio (definidas com o usuário antes de codar):**
- Estoque insuficiente **bloqueia** a venda, com mensagem informando a quantidade disponível
- Modelo de **carrinho persistido**: a própria `Venda` nasce com status `ABERTA` (carrinho) e só vira venda de verdade ao ser finalizada (`FINALIZADA`) — evita duplicar estrutura com uma entidade "Carrinho" separada
- Cancelamento/estorno **fora de escopo** por enquanto (só criar e consultar)
- Concorrência: **lock otimista** via `@Version` no `Produto`

- [x] Enum `StatusVenda` (ABERTA, FINALIZADA)
- [x] Entidade `Venda` (status, usuário, valorTotal, dataAbertura/Finalizacao, itens)
- [x] Entidade `ItemVenda` (produto, quantidade, precoUnitario — snapshot do preço no momento da venda)
- [x] Campo `@Version` adicionado ao `Produto` (lock otimista)
- [x] `VendaRepository` (`findByStatus`) — sem repository próprio para ItemVenda (sempre acessado via `venda.getItens()`, cascade + orphanRemoval cuidam de salvar/apagar)
- [x] `VendaService`: abrir, adicionar/atualizar/remover item, buscar, listar (filtro por status), finalizar
- [x] `VendaController`: `POST /vendas`, `POST/PUT/DELETE /vendas/{id}/itens[/{itemId}]`, `GET /vendas[/{id}]`, `POST /vendas/{id}/finalizar`
- [x] Exceções: `EstoqueInsuficienteException` (400), `OperacaoInvalidaException` (409), `VendaConcorrenteException` (409, traduz `ObjectOptimisticLockingFailureException`)
- [x] `@Transactional` em `finalizar()` — desconto de estoque de múltiplos produtos é tudo-ou-nada
- [x] Usuário da venda obtido via `@AuthenticationPrincipal Usuario`, nunca informado pelo cliente
- [x] Testado localmente de ponta a ponta (14 cenários, incluindo produto desativado, estoque insuficiente, venda já finalizada, carrinho vazio)
- [x] Roteiro formal `docs/qa/test-vendas.md`

**Bug de migração encontrado e corrigido:** `ddl-auto=update` tentou `ALTER TABLE produtos ADD COLUMN version bigint NOT NULL` sem valor padrão — falha em qualquer banco com produtos já cadastrados (aconteceu no ambiente local, que já tinha dados de teste da Fase 2). Corrigido com migração manual (`ALTER TABLE ... DEFAULT 0`) antes de subir a aplicação. **Produção (Neon) não foi afetada** porque a tabela `produtos` lá ainda estava vazia — mas o cuidado fica registrado para a próxima vez que uma coluna `NOT NULL` for adicionada a uma tabela com dados.

**Endpoints implementados:**
| Método | Rota | Descrição |
|---|---|---|
| POST | /vendas | Abre um carrinho novo |
| POST | /vendas/{id}/itens | Adiciona produto ao carrinho |
| PUT | /vendas/{id}/itens/{itemId} | Ajusta quantidade de um item |
| DELETE | /vendas/{id}/itens/{itemId} | Remove item do carrinho |
| GET | /vendas/{id} | Consulta uma venda |
| GET | /vendas | Lista vendas (filtro opcional `?status=`) |
| POST | /vendas/{id}/finalizar | Fecha o carrinho, desconta estoque |

---

### Fase 6 — Módulo Eventos ✅ CONCLUÍDA (2026-08-23)

**Decisões de negócio (definidas com o usuário em 2026-08-17, antes de codar):**
- Estoque **separado por evento** (não é mais só o `Produto.quantidadeEmEstoque` global da Fase 5) — nova entidade `EstoqueEvento`
- Vendas passam a exigir um evento em andamento — sem evento aberto, não dá pra abrir venda
- **1 evento `ABERTO` por vez**, vínculo automático: a venda nova já nasce vinculada ao evento aberto, sem o operador escolher
- Relatório **elaborado**: total arrecadado, quantidade de vendas, ticket médio, ranking de produtos mais vendidos
- Sistema continua **single-tenant** (uma instituição só) — multi-tenant vira [[Fase 9]], só depois do sistema validado em uso real

- [x] Enum `StatusEvento` (ABERTO, ENCERRADO)
- [x] Entidade `Evento` (nome, local, status, dataAbertura/dataEncerramento)
- [x] Entidade `EstoqueEvento` (ponte Evento↔Produto: quantidadeInicial/quantidadeAtual + `@Version`)
- [x] `Venda` ganha campo `evento` (nullable — vendas de antes da Fase 6 continuam válidas sem evento)
- [x] Refatorado `VendaService`: estoque agora é checado/descontado via `EstoqueEvento`, não mais `Produto` diretamente; `abrirVenda()` busca o evento `ABERTO` sozinho e recusa se não houver nenhum
- [x] `EventoService`/`EventoController`: criar (recusa se já existe outro `ABERTO`), encerrar (recusa se há vendas `ABERTA` pendentes), alocar/reforçar estoque, listar produtos do evento, relatório (total, qtd vendas, ticket médio, ranking de produtos mais vendidos)
- [x] Testado localmente de ponta a ponta — 19 cenários (evento único aberto, estoque por evento, venda vinculada automaticamente, relatório com valores conferidos, encerramento bloqueado por venda aberta, tudo trava depois de encerrado)
- [x] Roteiros formais `docs/qa/test-eventos.md` (12 testes) e `docs/qa/test-vendas.md` atualizado para o novo fluxo

**Endpoints implementados:**
| Método | Rota | Descrição |
|---|---|---|
| POST | /eventos | Cria e abre um evento (falha se já existe um aberto) |
| POST | /eventos/{id}/encerrar | Encerra o evento (falha se há vendas abertas) |
| GET | /eventos | Lista eventos |
| GET | /eventos/{id} | Consulta um evento |
| POST | /eventos/{id}/produtos | Aloca (soma) estoque de um produto pro evento |
| GET | /eventos/{id}/produtos | Lista produtos + estoque disponível no evento |
| GET | /eventos/{id}/relatorio | Total arrecadado, qtd vendas, ticket médio, produtos mais vendidos |

---

### Fase 7 — Gestão de Usuários ✅ CONCLUÍDA (2026-08-23)

**Mapeada em 2026-08-17**, a partir da lacuna encontrada com a senha placeholder do usuário ADMIN. Objetivo: dar autonomia de conta a quem usa o sistema, sem depender de SQL manual no Neon.

**Decisões de negócio (definidas com o usuário antes de codar):**
- Trocar a própria senha: **só a nova senha** — não exige confirmar a senha atual (o token JWT válido já é considerado prova de identidade suficiente)
- Reset de senha pelo ADMIN: **o ADMIN define a nova senha** diretamente, mesmo padrão já usado no cadastro (`POST /auth/register`)

- [x] `Usuario` ganha campo `ativo` (soft delete, mesmo padrão do `Produto`) — `isEnabled()` passou a refletir esse campo de verdade
- [x] `PUT /auth/me/senha` — usuário logado troca a própria senha
- [x] `GET /usuarios` — ADMIN lista usuários ativos
- [x] `DELETE /usuarios/{id}` — ADMIN desativa usuário; **bloqueia autodesativação** (evita ficar travado de fora do sistema)
- [x] `PUT /usuarios/{id}/senha` — ADMIN reseta a senha de outro usuário
- [x] **`JwtAuthFilter` atualizado**: um usuário desativado perde acesso a QUALQUER token já emitido imediatamente, não só a novos logins — sem isso, um token emitido antes da desativação continuaria válido até expirar sozinho (até 24h depois)
- [x] Testado localmente de ponta a ponta — 14 cenários, incluindo o teste crítico de segurança (mesmo token, antes/depois de desativar: `200` → `403`, sem novo login)
- [x] Roteiro formal `docs/qa/test-usuarios.md`

**Bug de migração evitado (aprendido na Fase 6):** `ativo` é uma coluna `NOT NULL` nova na tabela `usuarios`, que já tinha linhas cadastradas (local e produção). Migração manual (`ALTER TABLE usuarios ADD COLUMN ativo boolean NOT NULL DEFAULT true`) aplicada **antes** de subir a aplicação, tanto local quanto produção — dessa vez sem susto.

**Endpoints implementados:**
| Método | Rota | Auth | Descrição |
|---|---|---|---|
| PUT | /auth/me/senha | Qualquer usuário logado | Troca a própria senha |
| GET | /usuarios | ADMIN | Lista usuários ativos |
| DELETE | /usuarios/{id} | ADMIN | Desativa usuário (soft delete) |
| PUT | /usuarios/{id}/senha | ADMIN | Reseta a senha de outro usuário |

---

### Fase 8 — Frontend 🚧 EM ANDAMENTO (iniciada em 2026-08-23)

**Planejamento visual:** protótipo navegável (login + painel + PDV de vendas + eventos + produtos + usuários) construído e aprovado antes do código — identidade: verde profundo (`#1F5245`) + âmbar (`#C97B22`), tipografia Manrope + IBM Plex Mono pra números.

**Stack escolhida:**
| Camada | Escolha |
|---|---|
| Framework | React 19 + Vite 8 (JavaScript, não TypeScript — decisão deliberada pra não empilhar duas linguagens tipadas novas ao mesmo tempo) |
| Roteamento | React Router |
| Estilo | Tailwind CSS 4 |
| HTTP | fetch nativo (sem axios) |
| Estado | useState/useContext do React |
| Deploy (futuro) | Vercel ou Netlify, free tier |

**Configuração no backend (pré-requisito, feito):**
- [x] **CORS configurado** — `CorsConfigurationSource` no `SecurityConfig`, origem lida de `CORS_ALLOWED_ORIGINS` (fallback `http://localhost:5173` em dev)
- [x] **`GET /auth/me`** (novo) — o login só devolvia o token; esta rota devolve nome/email/cargo do usuário logado, pro frontend montar a tela

**Setup do projeto (feito):**
- [x] Projeto React criado em `frontend/cantinmoci` (Vite)
- [x] Tokens de cor/tipografia do protótipo aplicados em `src/index.css` (com suporte a tema claro/escuro automático)
- [x] Cliente HTTP central (`src/api/client.js`) — anexa o token JWT automaticamente, desloga sozinho em caso de 401
- [x] `AuthContext` — sessão do usuário, restaura do token salvo ao recarregar a página
- [x] Rotas protegidas (`ProtectedRoute`) — sem sessão válida, redireciona pro login
- [x] `AppShell` — menu lateral + barra superior, item "Usuários" só aparece pra ADMIN
- [x] **Tela de Login funcional de ponta a ponta** — testada no navegador (login real, `GET /auth/me`, logout, proteção de rota, tema escuro)

**Telas dos módulos (uma de cada vez):**
- [x] **Produtos** — listar, criar, editar e desativar (soft delete), testado no navegador de ponta a ponta (a desativação, que abre um `confirm()` nativo do navegador, foi validada via `curl` — a ferramenta de automação do navegador não consegue confirmar diálogos nativos)
- [x] **Eventos** — lista (`/eventos`) + detalhe (`/eventos/:id`) com estoque alocado, alocar/reforçar estoque, encerrar e relatório. Testado no navegador de ponta a ponta: criar evento, regra de "1 evento aberto por vez" (mensagem do backend aparecendo certinho), alocar estoque, relatório com valores batendo com os testes da Fase 6
- [x] **Bug corrigido:** `window.confirm()` nativo (usado em "desativar produto" e "encerrar evento") parava de funcionar depois de algumas confirmações seguidas — o Chrome oferece "impedir esta página de criar mais diálogos" e, se marcado, cancela todo `confirm()` seguinte sem nem mostrar a caixa. Reportado pelo usuário ("não consigo encerrar o evento"). Corrigido com `ConfirmDialog`, um modal de confirmação próprio do app
- [x] **Vendas (PDV)** — a tela mais completa: reaproveita o carrinho `ABERTA` já existente do operador (evita abandonar carrinhos ao recarregar a página), grade de produtos com estoque do evento em tempo real (desconta o que já está no carrinho, antes mesmo de confirmar no backend), clique soma quantidade na mesma linha em vez de duplicar, +/−/remover no carrinho, erro de estoque insuficiente exibido sem corromper o carrinho, finalizar mostra mensagem de sucesso e já abre um carrinho novo. Testado no navegador de ponta a ponta, inclusive o cenário de estoque esgotado bloqueando o card
- [x] **Usuários** — tela restrita a ADMIN (dupla proteção: menu escondido + guarda na própria página), lista usuários ativos, cadastra (`POST /auth/register`), reseta senha de outro (`PUT /usuarios/{id}/senha`), desativa com confirmação (botão nem aparece na própria linha, evita o 409 de autodesativação). "Trocar minha senha" fica no rodapé da barra lateral, acessível a qualquer cargo — reaproveita o mesmo modal genérico de senha. Testado no navegador de ponta a ponta, incluindo confirmação via `curl` de que a senha resetada/trocada realmente funciona no login
- [ ] Dashboard
- [ ] Deploy (Vercel/Netlify)

---

### Fase 9 — Multi-tenant / Organizações ⏳ FUTURA (planejada, não iniciada)

**Mapeada em 2026-08-17**, durante o planejamento da Fase 6. Objetivo: permitir que **várias instituições diferentes** usem a mesma instalação do sistema — cada uma com login próprio, e produtos/eventos/vendas completamente isolados das outras (a conta de uma instituição nunca vê nem interfere nos dados de outra).

**Quando priorizar:** só depois que o sistema de uma instituição só (o que estamos construindo agora) estiver **validado em uso real** — em eventos de verdade, não só em testes. Evita empilhar dois refactors grandes ao mesmo tempo e evita mexer em módulos já testados sem necessidade comprovada. Ver decisão completa em `docs/knowledge/decisions/multi-tenant.md`.

**Mudanças de arquitetura previstas:**
- [ ] Nova entidade `Organizacao`
- [ ] `Usuario`, `Produto`, `Evento` ganham `organizacaoId`
- [ ] Regra "1 evento aberto por vez" passa a ser por organização, não global
- [ ] Toda consulta (Produto, Evento, Venda) passa a filtrar pela organização do usuário logado
- [ ] Proteção contra vazamento entre contas (IDOR): busca por ID confere se o recurso pertence à organização de quem pede; se não pertence, retorna 404 (nunca 403 — não confirma que o recurso existe em outra conta)
- [ ] Novo endpoint público `POST /organizacoes` — cria uma organização nova + seu primeiro usuário ADMIN (hoje `/auth/register` exige um ADMIN pré-existente, o que não funciona pra uma instituição nova sem nenhum usuário ainda)
- [ ] Migração dos dados já existentes em produção (usuário, produtos, vendas de hoje) para uma organização "padrão/legado"

**Escala esperada:** dezenas de acessos simultâneos (20-100), não milhares — não exige infraestrutura mais robusta que a atual (Render + Neon free tier já comportam esse volume, mesmo com várias instituições).

**Custo:** continua zero — multi-tenant é decisão de modelagem de dados, não de infraestrutura.

**Caminho para escalar de verdade no futuro (pago), se um dia necessário:** a configuração via variável de ambiente e o `Dockerfile` (Fase 4) já deixam a troca para planos pagos (Neon/Render maiores, ou outro provedor) uma questão só de configuração — sem reescrever código. Multi-tenant bem feito agora facilita esse caminho, não atrapalha.

---

## Backlog de Melhorias Futuras

> Mapeado em 2026-08-17, numa varredura de lacunas em todo o projeto (Produto,
> Auth, Deploy). São itens legítimos para um sistema robusto, mas que **não
> bloqueiam** as próximas fases — fazem mais sentido depois do sistema
> validado com uso real em pelo menos um evento. Lista completa e priorizada
> em [`docs/backlog.md`](docs/backlog.md).

- Rate limiting / proteção contra força bruta no login
- Revogação de token JWT (logout de verdade — hoje um token vazado é válido até expirar sozinho)
- Auditoria — registrar quem fez cada venda/alteração
- Paginação nas listagens (produtos, vendas, usuários) — hoje devolvem tudo de uma vez
- Monitoramento/alerta de uptime (ex: UptimeRobot gratuito)
- Estratégia de backup do banco (o free tier do Neon tem retenção limitada)
- Política de senha mais forte que "mínimo 6 caracteres"
- Tratamento de erro padronizado (`@ControllerAdvice` global, em vez de uma `@ResponseStatus` por exceção)

---

## Decisões Técnicas Registradas

| Data | Decisão | Motivo |
|---|---|---|
| 2026-05-29 | Java 21 LTS como padrão (não o 26) | Compatibilidade garantida com Spring Boot 3.x, suporte longo |
| 2026-05-29 | Maven como build tool (não Gradle) | Mais simples para aprendizado, mais documentação disponível |
| 2026-05-29 | PostgreSQL 17 | Banco robusto, melhor suporte com Spring Boot/JPA |
| 2026-05-29 | Maven instalado em `%USERPROFILE%\dev-tools` | Evitar necessidade de permissão de admin |
| 2026-05-29 | Spring Boot 3.5.14 (não 4.x) | Versão mais estável com maior documentação disponível |
| 2026-05-29 | `application.properties` no `.gitignore` | Proteger senha do banco de dados no GitHub |
| 2026-08-17 | Deploy gratuito via Render (backend, Docker) + Neon (PostgreSQL) | Sistema usado esporadicamente em eventos beneficentes — não exige disponibilidade 24/7 nem justifica custo de servidor ou domínio próprio |

---

## Padrões de Commit Git

```
feat: descrição    → nova funcionalidade
fix: descrição     → correção de bug
refactor: descrição → melhoria de código sem nova feature
docs: descrição    → atualização de documentação
config: descrição  → mudança de configuração
```

---

## Como Iniciar o Projeto (após Fase 1)

```bash
# Entrar na pasta do backend
cd cantinmoci/backend

# Rodar o projeto
mvn spring-boot:run

# O servidor sobe em: http://localhost:8080
```

---

## Observações para a IA em futuras sessões

- O usuário está aprendendo Java e Spring Boot — explique cada conceito antes de escrever código.
- Nunca gere código sem explicação. Explique o que cada parte faz.
- Siga sempre o fluxo: Planejamento → Modelagem → Backend → Banco → Testes → Frontend → Revisão → Docs.
- Um módulo de cada vez. Não adiantar etapas.
- O usuário quer desenvolver independência — guie, não faça por ele.
