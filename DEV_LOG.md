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

### Fase 3 — Módulo Autenticação ✅ IMPLEMENTADA (validada em produção; roteiro Postman formal ainda pendente)

- [x] Enum Cargo (ADMIN, OPERADOR)
- [x] Entidade Usuario (id, nome, email, senha hash BCrypt, cargo) — implementa UserDetails
- [x] UsuarioRepository — findByEmail(String)
- [x] DTOs: LoginRequestDTO, TokenResponseDTO
- [x] JwtService — gerar/extrair/validar token HS256 com expiração 24h
- [x] UserDetailsServiceImpl — loadUserByUsername por email
- [x] AuthService — autenticar com BCrypt + gerar token
- [x] JwtAuthFilter — intercepta header Authorization: Bearer
- [x] SecurityConfig — rotas públicas: /health e /auth/login; demais: autenticadas; STATELESS
- [x] AuthController — POST /auth/login
- [x] Dependências JWT e Spring Security adicionadas ao pom.xml
- [x] Tabela `usuarios` criada automaticamente pelo Hibernate
- [x] Validado em produção (curl): login retorna token, `/produtos` aceita o token e recusa sem ele
- [ ] Roteiro formal `docs/qa/test-auth.md` (padrão do `test-produto.md`) — pendente

**Endpoints implementados:**
| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | /auth/login | Recebe email+senha, retorna token JWT | Pública |

**Rotas protegidas (exigem Bearer token):**
| Método | Rota |
|---|---|
| GET | /produtos |
| GET | /produtos/{id} |
| POST | /produtos |
| PUT | /produtos/{id} |
| DELETE | /produtos/{id} |

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

### Fase 5 — Módulo Vendas + Estoque ⏳ NÃO INICIADA

- [ ] Entidades Venda e ItemVenda
- [ ] Carrinho e finalização de venda
- [ ] Baixa automática de estoque
- [ ] Histórico de vendas

---

### Fase 6 — Módulo Eventos ⏳ NÃO INICIADA

- [ ] Entidade Evento
- [ ] Isolamento de produtos e vendas por evento
- [ ] Relatórios por evento

---

### Fase 7 — Frontend ⏳ NÃO INICIADA

- [ ] Definir tecnologia
- [ ] Telas principais

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
