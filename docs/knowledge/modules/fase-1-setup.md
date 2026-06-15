# Fase 1 — Setup do Projeto

Tags: #modulo #concluido #setup

Status: ✅ Concluída

---

## O que foi feito
- Projeto Spring Boot criado via Spring Initializr
- Conexão com PostgreSQL configurada
- Banco de dados `cantinmoci` criado
- Endpoint `GET /health` funcionando
- Primeiro commit no Git

## Dependências adicionadas
- Spring Web — APIs REST
- Spring Data JPA — comunicação com banco via Java
- PostgreSQL Driver — conexão com banco
- Spring Boot DevTools — restart automático

## Arquivo principal
- `backend/cantinmoci/src/main/java/com/cantinmoci/controller/HealthController.java`

## Como rodar
```bash
cd backend/cantinmoci
mvnw.cmd spring-boot:run
# http://localhost:8080/health
```

## Relacionado
- [[stack]]
- [[arquitetura]]
- [[fase-2-produto]]