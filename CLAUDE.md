# CantinMoci — Contexto Global

## O que é este projeto
Sistema web de gerenciamento de cantinas em eventos beneficentes.
Projeto de aprendizado prático em Java + Spring Boot + PostgreSQL.

## Stack
- Backend: Java 21 + Spring Boot 3.5 + Maven
- Banco: PostgreSQL 17 (porta 5432, usuário: postgres)
- Testes de API: Postman
- OS: Windows 11

## Estrutura de pastas
- backend/cantinmoci/ → projeto Spring Boot
- docs/ → documentação técnica
- database/ → scripts SQL
- DEV_LOG.md → progresso atual do projeto

## Status atual
- Fase 0 ✅ Ambiente configurado
- Fase 1 ✅ Spring Boot rodando, endpoint /health ok
- Fase 2 ✅ CRUD de Produto implementado e testado (10 testes Postman passando)
- Fase 3 ⏳ PRÓXIMA — Módulo Autenticação (Spring Security + JWT)

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