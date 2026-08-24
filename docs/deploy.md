# Deploy Gratuito — CantinMoci (Fase 4)

> Objetivo: deixar o backend acessível por uma URL pública, sem custo e sem
> depender de um computador pessoal ligado. Ver decisão completa em
> `docs/knowledge/decisions/deploy.md`.

Arquitetura:

```
GitHub (seu repositório)
   │  push
   ▼
Render (Web Service, free tier) ──── builda a imagem via Dockerfile
   │
   │  JDBC (variáveis de ambiente)
   ▼
Neon (PostgreSQL, free tier)
```

Este guia está dividido em duas partes:
- **Parte A** — o que já foi preparado no código (feito, nada pra você fazer aqui)
- **Parte B** — o que precisa ser feito nos painéis do Neon e do Render (sua parte)

---

## Parte A — O que já foi preparado no código

- `application.properties` agora lê configurações sensíveis de variáveis de
  ambiente, com fallback pro valor local — seu ambiente continua funcionando
  exatamente como antes, sem precisar configurar nada agora.
- `application.properties` agora é versionado no Git — não tem mais nenhum
  segredo, só `${VAR}` sem valor padrão sensível. Os segredos de
  desenvolvimento local ficam isolados em `application-local.properties`
  (fora do Git, importado automaticamente via `spring.config.import`) — é
  esse isolamento que torna seguro versionar o arquivo principal.
- `Dockerfile` (em `backend/cantinmoci/Dockerfile`) builda a aplicação com
  Java 21 e gera a imagem que o Render vai rodar — isso evita depender do
  Render "adivinhar" a versão certa do Java.

Variáveis de ambiente que a aplicação espera em produção:

| Variável | O que é | Exemplo |
|---|---|---|
| `DATABASE_URL` | URL JDBC do Postgres (sem usuário/senha embutidos) | `jdbc:postgresql://ep-xxxx.neon.tech/cantinmoci?sslmode=require` |
| `DATABASE_USERNAME` | Usuário do banco no Neon | `cantinmoci_owner` |
| `DATABASE_PASSWORD` | Senha do banco no Neon | (gerada pelo Neon) |
| `JWT_SECRET` | Chave para assinar os tokens — **diferente** da usada localmente | (gere uma nova, veja abaixo) |
| `JWT_EXPIRATION` | Validade do token em ms | `86400000` (24h) |
| `PORT` | Porta que a app deve escutar | O Render injeta isso sozinho — não precisa cadastrar |

Para gerar um `JWT_SECRET` novo (Base64, 32+ bytes), você pode rodar isso no seu terminal quando chegar nessa etapa:

```bash
openssl rand -base64 32
```

---

## Parte B — O que você precisa fazer

### B.1 — Criar o banco de dados no Neon

1. Acesse https://neon.tech e crie uma conta gratuita (dá pra usar login do GitHub, facilita).
2. Crie um novo projeto — pode chamar de `cantinmoci`, região mais próxima de você.
3. O Neon já cria um banco padrão. Na tela do projeto, procure por **"Connection Details"** (ou "Connection String").
4. Copie a connection string. Ela tem esse formato:
   ```
   postgresql://usuario:senha@ep-xxxxx.neon.tech/nomedobanco?sslmode=require
   ```
5. Dessa string, você vai separar 3 valores (vamos usar no Render, na etapa B.3):
   - **DATABASE_URL** → `jdbc:postgresql://ep-xxxxx.neon.tech/nomedobanco?sslmode=require`
     (é a mesma string, só troca `postgresql://usuario:senha@` por `jdbc:postgresql://`)
   - **DATABASE_USERNAME** → a parte `usuario`
   - **DATABASE_PASSWORD** → a parte `senha`
6. Guarde esses 3 valores num lugar seguro (bloco de notas temporário) — não me envie a senha, e não commite em nenhum arquivo.

### B.2 — Criar conta no Render e conectar o repositório

1. Acesse https://render.com e crie uma conta gratuita (recomendo logar com GitHub — facilita conectar o repositório).
2. No Dashboard, clique em **"New +"** → **"Web Service"**.
3. Autorize o Render a acessar seu GitHub e selecione o repositório do CantinMoci.
4. Configure o serviço:
   - **Name:** `cantinmoci-backend` (ou o nome que preferir)
   - **Root Directory:** `backend/cantinmoci`
   - **Runtime:** Docker (o Render deve detectar o `Dockerfile` automaticamente nessa pasta)
   - **Instance Type:** Free

> A interface do Render muda de tempos em tempos — se algum nome de campo
> estiver diferente do que descrevi, procure o equivalente (ex.: "Language"
> em vez de "Runtime"). O importante é garantir que ele use o `Dockerfile`
> dentro de `backend/cantinmoci`.

### B.3 — Cadastrar as variáveis de ambiente

Antes de criar o serviço (ou logo depois, em **"Environment"**), cadastre as variáveis da tabela da Parte A:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET` (o novo valor gerado com `openssl rand -base64 32`)
- `JWT_EXPIRATION` = `86400000`

Não precisa cadastrar `PORT` — o Render injeta essa automaticamente.

### B.4 — Rodar o primeiro deploy

1. Clique em **"Create Web Service"** (ou "Deploy").
2. Acompanhe o log de build — a primeira vez demora mais (build da imagem Docker + download de dependências).
3. Quando terminar, o Render mostra uma URL pública, algo como:
   ```
   https://cantinmoci-backend.onrender.com
   ```

---

## Parte C — Validar

1. Abra no navegador (ou Postman): `GET https://SEU-APP.onrender.com/health` → deve responder OK.
2. No Postman, teste `POST /auth/login` contra essa mesma URL.
3. Teste os endpoints de `/produtos` (lembrando: exigem o token JWT retornado pelo login).
4. Se der tudo certo, me avisa que eu atualizo o DEV_LOG marcando a Fase 4 como concluída.

## Troubleshooting comum

- **Build falha no Render:** confira o log — geralmente é caminho errado no "Root Directory" ou o Dockerfile não foi encontrado.
- **App sobe mas `/health` dá erro de conexão com banco:** confira se `DATABASE_URL`/`DATABASE_USERNAME`/`DATABASE_PASSWORD` foram cadastrados certinho (sem espaços extras) e se a URL do Neon inclui `?sslmode=require`.
- **Primeira requisição do dia demora ~30-50s:** normal no free tier do Render (a app "dorme" após inatividade). Não é erro.

## Próximos passos (depois que isso estiver validado)

- Se um dia quiser um domínio próprio (ex: `cantinmoci.com.br`), dá pra apontar pro Render depois — não é necessário agora.

---

# Deploy do Frontend (Fase 8) — Vercel

> Objetivo: publicar o frontend numa URL pública, gratuita, com deploy
> automático a cada push. Continua a mesma filosofia zero-custo da Fase 4.

Arquitetura:

```
GitHub (seu repositório)
   │  push
   ▼
Vercel (Static Site, free tier) ── builda com Vite (npm run build)
   │
   │  fetch (VITE_API_URL)
   ▼
Render (backend, já no ar)
```

Este guia segue o mesmo formato da Fase 4: **Parte A** é o que já foi
preparado no código, **Parte B** é o que precisa ser feito no painel da
Vercel (sua parte), **Parte C** valida o resultado.

## Parte A — O que já foi preparado no código

- `frontend/cantinmoci/.env.production` — define `VITE_API_URL` apontando
  pro backend em produção (`https://cantinmoci.onrender.com`). O Vite troca
  automaticamente para esse arquivo quando builda em modo produção
  (`npm run build`), sem precisar de nenhuma configuração extra na Vercel.
  Não tem segredo nenhum aqui — é só uma URL pública, por isso pode ficar
  versionado no Git (mesmo padrão do `.env.development`).
- `frontend/cantinmoci/vercel.json` — o app usa React Router no modo
  padrão (rotas tipo `/eventos/5` não existem como arquivo físico). Sem
  essa configuração, recarregar a página numa rota assim daria 404 na
  Vercel. Esse arquivo redireciona qualquer rota pro `index.html`, deixando
  o React Router assumir a partir daí.
- Testado localmente: `npm run build` gera o `dist/` corretamente e o
  código buildado já aponta pro backend de produção (confirmado inspecionando
  o JS gerado).

## Parte B — O que você precisa fazer

### B.1 — Criar conta na Vercel e importar o repositório

1. Acesse https://vercel.com e crie uma conta gratuita (recomendo logar
   com GitHub — facilita conectar o repositório, igual fizemos no Render).
2. No Dashboard, clique em **"Add New..."** → **"Project"**.
3. Selecione o repositório do CantinMoci na lista (autorize o acesso da
   Vercel ao GitHub se pedir).

### B.2 — Configurar o projeto

Quando a Vercel pedir as configurações do projeto:

- **Framework Preset:** Vite (a Vercel geralmente detecta sozinha)
- **Root Directory:** `frontend/cantinmoci` — **esse é o passo que mais
  costuma passar despercebido**, o repositório tem backend e frontend juntos,
  então é essencial apontar pra subpasta certa
- **Build Command:** `npm run build` (padrão, não precisa mexer)
- **Output Directory:** `dist` (padrão, não precisa mexer)

> Não precisa cadastrar nenhuma variável de ambiente na Vercel — o
> `VITE_API_URL` já vem do `.env.production` versionado no repositório.

### B.3 — Rodar o primeiro deploy

1. Clique em **"Deploy"**.
2. Acompanhe o log de build (bem mais rápido que o do Render — sem Docker).
3. Quando terminar, a Vercel mostra uma URL pública, algo como:
   ```
   https://cantinmoci.vercel.app
   ```
   (o nome exato depende do que ficar disponível/do nome do projeto)

### B.4 — Liberar o CORS no backend pra essa URL

Esse passo é essencial — sem ele, o navegador bloqueia toda chamada do
frontend novo pra API (mesmo problema que resolvemos em localhost na Fase 8,
agora pro domínio de produção):

1. Copie a URL que a Vercel te deu (ex: `https://cantinmoci.vercel.app`).
2. No painel do **Render**, abra o serviço do backend → **"Environment"**.
3. Edite a variável `CORS_ALLOWED_ORIGINS` (se ainda não existir, crie) com
   o valor:
   ```
   https://cantinmoci.vercel.app
   ```
   Se um dia precisar liberar mais de um domínio, separe por vírgula, sem
   espaço: `https://dominio1.com,https://dominio2.com`.
4. Salve — o Render reinicia o serviço sozinho aplicando a variável nova.

## Parte C — Validar

1. Abra a URL da Vercel no navegador.
2. Faça login com um usuário real.
3. Abra o DevTools (F12) → aba "Console" — não deve aparecer nenhum erro
   de CORS.
4. Navegue por Produtos, Eventos, Vendas, Usuários, Dashboard — confirme
   que tudo carrega dados reais do backend em produção.
5. Se der tudo certo, me avisa que eu atualizo o DEV_LOG marcando a Fase 8
   como concluída.

## Troubleshooting comum

- **Build falha na Vercel:** confira se o "Root Directory" está mesmo como
  `frontend/cantinmoci` — é o erro mais comum em repositórios com backend e
  frontend juntos.
- **Tela em branco ou 404 ao recarregar uma rota (`/eventos/5`):** confirma
  se o `vercel.json` foi detectado (ele precisa estar dentro da Root
  Directory configurada).
- **Erro de CORS no console do navegador:** confirme que `CORS_ALLOWED_ORIGINS`
  no Render tem a URL **exata** da Vercel, sem barra `/` no final.
- **Login funciona mas as telas ficam "Carregando…" pra sempre:** normalmente
  é o Render "dormindo" (mesmo comportamento do free tier explicado na Fase
  4) — a primeira requisição depois de um tempo parado demora ~30-50s.
