# Fase 7 — Gestão de Usuários

Tags: #modulo #concluido #usuarios #seguranca

Status: ✅ Concluída (2026-08-23) — testada localmente, 14 cenários

---

## Decisões de negócio (definidas antes de codar)

- Trocar a própria senha: **só a nova senha**, sem confirmar a senha atual — o token JWT válido já é considerado prova de identidade suficiente
- Reset de senha pelo ADMIN: **o ADMIN define a nova senha** diretamente (mesmo padrão do cadastro, `POST /auth/register`)

## O que foi implementado

- `Usuario` ganha campo `ativo` (soft delete, mesmo padrão do `Produto`)
- `Usuario.isEnabled()` passou a refletir esse campo de verdade (antes retornava sempre `true`)
- **`JwtAuthFilter` atualizado** — checa `userDetails.isEnabled()` antes de validar o token. Sem isso, desativar um usuário só impediria login *novo*; um token já emitido continuaria funcionando até expirar sozinho (até 24h). Agora a desativação revoga acesso imediatamente, mesmo com um token ainda "válido" tecnicamente.
- `AuthService.autenticar()` também confere `ativo` explicitamente (o projeto não usa o `AuthenticationManager` padrão do Spring, que checaria `isEnabled()` sozinho — o login é manual)
- `AuthService`: `trocarSenha`, `resetarSenha`, `listarAtivos`, `desativar` (bloqueia autodesativação)
- `AuthController`: `PUT /auth/me/senha`
- `UsuarioController` (novo): `GET /usuarios`, `DELETE /usuarios/{id}`, `PUT /usuarios/{id}/senha` — todas restritas a ADMIN via `SecurityConfig`

## Endpoints implementados

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| PUT | /auth/me/senha | Qualquer usuário logado | Troca a própria senha |
| GET | /usuarios | ADMIN | Lista usuários ativos |
| DELETE | /usuarios/{id} | ADMIN | Desativa usuário (soft delete) |
| PUT | /usuarios/{id}/senha | ADMIN | Reseta a senha de outro usuário |

## Arquivos principais

- `backend/cantinmoci/src/main/java/com/cantinmoci/model/Usuario.java` (campo `ativo`, `isEnabled()`)
- `backend/cantinmoci/src/main/java/com/cantinmoci/config/JwtAuthFilter.java` (checagem de `isEnabled()`)
- `backend/cantinmoci/src/main/java/com/cantinmoci/service/AuthService.java` (métodos novos)
- `backend/cantinmoci/src/main/java/com/cantinmoci/controller/UsuarioController.java` (novo)
- `backend/cantinmoci/src/main/java/com/cantinmoci/dto/TrocarSenhaDTO.java`, `ResetarSenhaDTO.java`

## Bug de migração evitado

`ativo` é coluna `NOT NULL` nova em `usuarios`, tabela que já tinha linhas (local e produção) — mesma classe de problema já vivida na Fase 6 com `Produto.version`. Desta vez a migração manual (`ALTER TABLE usuarios ADD COLUMN ativo boolean NOT NULL DEFAULT true`) foi aplicada **antes** do deploy, tanto local quanto direto no Neon de produção — sem repetir o susto.

## Roteiro de testes

- Arquivo: `docs/qa/test-usuarios.md` (10 testes)
- Cenário mais importante: desativar um usuário revoga o token dele **na hora** — testado literalmente reusando o mesmo token antes/depois da desativação (`200` → `403`, sem novo login)

## Relacionado

- [[fase-3-auth]]
- [[fase-9-multi-tenant]]
- [[Spring-Security]]
- [[arquitetura]]
