# CantinMoci — Contexto Global

## O que é este projeto
Sistema web de gerenciamento de cantinas em eventos beneficentes.
Projeto de aprendizado prático em Java + Spring Boot + PostgreSQL.

## Stack
- Backend: Java 21 + Spring Boot 3.5 + Maven
- Banco: PostgreSQL 17 (porta 5432, usuário: postgres) — local; em produção via Neon
- Hospedagem: Render (backend, free tier, via Docker) + Neon (PostgreSQL, free tier) — no ar em https://cantinmoci.onrender.com, sem custo, sem domínio próprio, ver `docs/deploy.md`
- Testes de API: Postman
- OS: Windows 11

## Estrutura de pastas
- backend/cantinmoci/ → projeto Spring Boot
- docs/ → documentação técnica
- docs/backlog.md → melhorias mapeadas, não bloqueantes, para depois do sistema validado em evento real
- database/ → scripts SQL
- DEV_LOG.md → progresso atual do projeto

## Status atual
- Fase 0 ✅ Ambiente configurado
- Fase 1 ✅ Spring Boot rodando, endpoint /health ok
- Fase 2 ✅ CRUD de Produto implementado e testado (10 testes Postman passando)
- Fase 3 ✅ Módulo Autenticação concluído (Spring Security + JWT, cadastro restrito a ADMIN, 401 correto, testado em produção)
- Fase 4 ✅ Deploy Gratuito no ar — https://cantinmoci.onrender.com (Render + Neon)
- Fase 5 ⏳ PRÓXIMA — Módulo Vendas + Estoque
- Fase 6 — Módulo Eventos
- Fase 7 — Gestão de Usuários (trocar senha, listar/desativar, admin resetar senha)
- Fase 8 — Frontend (requer configurar CORS)

## Regras inegociáveis
1. Explique SEMPRE antes de escrever código
2. Um módulo de cada vez
3. Fluxo obrigatório: Planejamento → Modelagem → Backend → Banco → Testes → Revisão → Docs
4. O usuário está aprendendo Java — nunca assuma conhecimento prévio
5. Commits pequenos com padrão: feat/fix/refactor/docs/config

## Arquitetura backend esperada
controllers/ → services/ → repositories/ → models/ → dtos/ → config/ → exceptions/

## Como rodar o projeto
cd backend/cantinmoci
mvnw.cmd spring-boot:run
# Sobe em http://localhost:8080