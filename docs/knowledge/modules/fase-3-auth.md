# Fase 3 — Módulo Autenticação (JWT)

Tags: #modulo #concluido #seguranca #jwt

Status: ✅ Concluída (fechada em 2026-08-17 — pendências resolvidas após o deploy)

---

## O que foi implementado

- Enum `Cargo` (ADMIN, OPERADOR)
- Entidade `Usuario` (id, nome, email, senha hash BCrypt, cargo) — implementa `UserDetails`
- `UsuarioRepository` — `findByEmail(String)`, `existsByEmail(String)`
- `JwtService` — gera/extrai/valida token HS256, expiração 24h
- `UserDetailsServiceImpl` — carrega usuário pelo email
- `AuthService` — autentica com BCrypt, gera token, cadastra novos usuários
- `JwtAuthFilter` — intercepta header `Authorization: Bearer`
- `SecurityConfig` — rotas públicas: `/health`, `/auth/login`, `/error`; `/auth/register` exige ADMIN; demais autenticadas; sessão STATELESS
- `UnauthorizedException` e `EmailJaCadastradoException` — exceções customizadas com `@ResponseStatus`, mesmo padrão do `ResourceNotFoundException` da Fase 2

## Endpoints implementados

| Método | Rota | Auth | Retorno sucesso |
|---|---|---|---|
| POST | /auth/login | Pública | 200 + `{ "token": "..." }` |
| POST | /auth/register | Exige ADMIN | 201 + dados do usuário criado (sem senha) |

## Rotas protegidas (exigem Bearer token)

| Método | Rota |
|---|---|
| GET/POST/PUT/DELETE | /produtos, /produtos/{id} |
| POST | /auth/register (exige especificamente cargo ADMIN) |

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
- `backend/cantinmoci/src/main/java/com/cantinmoci/exception/UnauthorizedException.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/exception/EmailJaCadastradoException.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/dto/RegisterRequestDTO.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/dto/UsuarioResponseDTO.java`

## Pendências resolvidas (2026-08-17)

- **Endpoint de cadastro criado** (`POST /auth/register`) — restrito a usuários ADMIN, evita autocadastro com qualquer cargo. Sem essa restrição, qualquer pessoa poderia se cadastrar como ADMIN.
- **Login inválido agora retorna 401** (antes: 500) — criada `UnauthorizedException` com `@ResponseStatus(UNAUTHORIZED)`.
- **Bug extra encontrado durante a correção do 401:** a resposta continuava vindo como 403 mesmo depois da correção. Causa: o Spring Security também filtra o redirecionamento interno para `/error` (usado para montar o corpo da resposta de erro), e essa rota não estava liberada — o segundo filtro sobrescrevia qualquer status de erro para 403. Corrigido liberando `/error` como rota pública no `SecurityConfig`.
- **Roteiro formal de testes criado:** `docs/qa/test-auth.md`, 11 testes cobrindo login (sucesso, senha errada, email inexistente), rotas protegidas (com/sem token), cadastro (sucesso, email duplicado, sem token, sem ser ADMIN, body inválido).

## Relacionado

- [[fase-2-produto]]
- [[fase-4-deploy]]
- [[arquitetura]]
- [[Spring-Security]]
