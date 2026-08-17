# Fase 3 — Módulo Autenticação (JWT)

Tags: #modulo #implementado #seguranca #jwt

Status: 🚧 Implementada — aguardando testes Postman (código commitado em 2026-08-17)

---

## O que foi implementado

- Enum `Cargo` (ADMIN, OPERADOR)
- Entidade `Usuario` (id, nome, email, senha hash BCrypt, cargo) — implementa `UserDetails`
- `UsuarioRepository` — `findByEmail(String)`
- `JwtService` — gera/extrai/valida token HS256, expiração 24h
- `UserDetailsServiceImpl` — carrega usuário pelo email
- `AuthService` — autentica com BCrypt e gera token
- `JwtAuthFilter` — intercepta header `Authorization: Bearer`
- `SecurityConfig` — rotas públicas: `/health` e `/auth/login`; demais autenticadas; sessão STATELESS

## Endpoints implementados

| Método | Rota | Auth | Retorno sucesso |
|---|---|---|---|
| POST | /auth/login | Pública | 200 + `{ "token": "..." }` |

## Rotas protegidas (exigem Bearer token)

| Método | Rota |
|---|---|
| GET/POST/PUT/DELETE | /produtos, /produtos/{id} |

## Arquivos principais

- `backend/cantinmoci/src/main/java/com/cantinmoci/config/SecurityConfig.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/config/JwtAuthFilter.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/controller/AuthController.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/service/AuthService.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/service/JwtService.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/service/UserDetailsServiceImpl.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/model/Usuario.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/model/Cargo.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/repository/UsuarioRepository.java`

## Pendências conhecidas

- Sem endpoint de cadastro de usuário — usuário de teste precisa ser inserido manualmente no banco por enquanto
- Login inválido retorna 500 em vez de 401 (tratamento de erro a melhorar em revisão futura)
- Testes Postman ainda não executados — roteiro `docs/qa/test-auth.md` ainda não existe

## Relacionado

- [[fase-2-produto]]
- [[fase-4-deploy]]
- [[arquitetura]]
- [[Spring-Security]]
