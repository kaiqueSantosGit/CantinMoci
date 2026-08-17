# Fase 4 — Deploy Gratuito (Infraestrutura)

Tags: #modulo #infraestrutura #deploy #proxima

Status: ⏳ Próxima — preparação de código concluída, configuração na nuvem pendente (usuário)

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
- `application.properties.example` criado e versionado, documentando as variáveis exigidas em produção
- `server.port` passou a ler da variável `PORT` (padrão exigido pelo Render)
- `Dockerfile` multi-stage criado — garante Java 21 em qualquer plataforma de deploy, independente do buildpack nativo

## O que falta (usuário, com passo a passo)

Guia completo em `docs/deploy.md`:
1. Criar banco PostgreSQL gratuito no Neon
2. Criar conta no Render e conectar o repositório GitHub
3. Configurar o Web Service (runtime Docker) e as variáveis de ambiente
4. Rodar o primeiro deploy
5. Validar `/health`, `/auth/login` e `/produtos` na URL pública

## Relacionado

- [[fase-3-auth]]
- [[stack]]
- [[deploy]]
