# Fase 4 — Deploy Gratuito (Infraestrutura)

Tags: #modulo #infraestrutura #deploy #proxima

Status: ✅ Concluída (2026-08-17) — no ar em https://cantinmoci.onrender.com

---

## Objetivo

Deixar o backend acessível publicamente pela internet, sem custo e sem depender de uma máquina pessoal ligada — para testar via Postman contra uma URL pública e futuramente usar em eventos reais.

## Arquitetura escolhida

- Backend → **Render** (free tier, deploy automático a partir do GitHub, via Docker)
- Banco de Dados → **Neon** (PostgreSQL gerenciado, free tier)
- Sem domínio próprio — subdomínio gratuito da plataforma (`*.onrender.com`)

## Por que essa escolha

- Uso esporádico (eventos), não contínuo — o "cold start" do free tier do Render (app dorme após ~15 min sem tráfego e demora ~30-50s pra acordar) é um trade-off aceitável nesse cenário
- Zero custo, sem cartão de crédito exigido nos free tiers do Render e Neon
- Não depende de nenhum computador pessoal ficar ligado

## O que foi preparado no código

- `application.properties` passou a ler configuração sensível de variáveis de ambiente, com fallback para os valores locais (`${VAR:default}`) — não quebra o ambiente de desenvolvimento local
- `application.properties` passou a ser versionado no Git (sem segredo nenhum); segredos locais isolados em `application-local.properties` (fora do Git), importado automaticamente via `spring.config.import=optional:...`
- `server.port` passou a ler da variável `PORT` (padrão exigido pelo Render)
- `Dockerfile` multi-stage criado — garante Java 21 em qualquer plataforma de deploy, independente do buildpack nativo

## Configuração realizada

- Neon: projeto `CantiMoci`, banco `neondb`, conexão direta (sem pooler)
- Render: Web Service `CantinMoci`, runtime Docker, root directory `backend/cantinmoci`, região Oregon
- Variáveis cadastradas no Render: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`

## Bug encontrado e corrigido

1ª tentativa de deploy falhou com `Failed to configure a DataSource`. Causa: `application.properties` estava no `.gitignore` (protegendo a senha local) e por isso nunca chegava à imagem Docker buildada pelo Render — o jar subia sem nenhuma configuração de banco. Corrigido separando configuração versionada (`application.properties`, sem segredo) de segredos locais (`application-local.properties`, fora do Git, importado via `spring.config.import=optional:...`). Commit `d93aa1f`.

## Validação

- `GET /health` → 200 OK
- `GET /produtos` sem token → 403 Forbidden
- `POST /auth/login` com usuário de teste (inserido via SQL no Neon) → 200 OK + token
- `GET /produtos` com token → 200 OK

## Relacionado

- [[fase-3-auth]]
- [[stack]]
- [[deploy]]
