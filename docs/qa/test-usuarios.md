# Roteiro de Testes Manuais — Módulo Gestão de Usuários

> Execute os testes NA ORDEM apresentada. Cada teste depende dos dados criados pelo anterior.
> API local: http://localhost:8080 — API em produção: https://cantinmoci.onrender.com

---

## Antes de começar

1. Faça login como ADMIN e guarde o token (`{tokenAdmin}`).

---

## TESTE 01 — ADMIN lista usuários ativos

**Configuração:** `GET /usuarios`, header `Authorization: Bearer {tokenAdmin}`.

**Resposta esperada:** `200 OK`, array com pelo menos o próprio ADMIN:
```json
[{ "id": 1, "nome": "...", "email": "...", "cargo": "ADMIN", "ativo": true }]
```

---

## TESTE 02 — Cadastrar um usuário OPERADOR de teste

**Configuração:** `POST /auth/register` (já existente desde a Fase 3), header `Authorization: Bearer {tokenAdmin}`.
```json
{ "nome": "Operador Teste", "email": "operador@teste.com", "senha": "senha123", "cargo": "OPERADOR" }
```

**Resposta esperada:** `201 Created`. **Anote o `id`.**

---

## TESTE 03 — OPERADOR não pode listar usuários

**O que valida:** `GET /usuarios` é restrito a ADMIN.

**Configuração:** faça login com o operador do Teste 02, guarde o token (`{tokenOperador}`), e chame `GET /usuarios` com esse token.

**Resposta esperada:** `403 Forbidden`.

---

## TESTE 04 — Trocar a própria senha

**Configuração:** `PUT /auth/me/senha`, header `Authorization: Bearer {tokenOperador}`.
```json
{ "novaSenha": "senhaNova456" }
```

**Resposta esperada:** `204 No Content`.

---

## TESTE 05 — Confirmar a troca: login com senha antiga falha, com a nova funciona

**Configuração:**
- `POST /auth/login` com a senha antiga (`senha123`) → esperado `401`
- `POST /auth/login` com a senha nova (`senhaNova456`) → esperado `200`

---

## TESTE 06 — ADMIN reseta a senha do operador

**Configuração:** `PUT /usuarios/{id}/senha` (o `id` do Teste 02), header `Authorization: Bearer {tokenAdmin}`.
```json
{ "novaSenha": "senhaResetadaPeloAdmin789" }
```

**Resposta esperada:** `204 No Content`. Depois, `POST /auth/login` com essa senha deve dar `200 OK`.

---

## TESTE 07 — ADMIN não pode se autodesativar

**O que valida:** proteção contra ficar travado de fora do sistema por engano.

**Configuração:** `DELETE /usuarios/{idDoProprioAdmin}`, header `Authorization: Bearer {tokenAdmin}`.

**Resposta esperada:** `409 Conflict`, mensagem `"Voce nao pode desativar a propria conta"`.

---

## TESTE 08 — Desativar o operador — o token dele perde acesso IMEDIATAMENTE

**O que valida:** o ponto de segurança mais importante desta fase — um usuário desativado não consegue mais usar nenhum token já emitido, mesmo sem ele ter expirado.

**Passos:**
1. Com `{tokenOperador}` (ainda válido), chame `GET /produtos` → esperado `200 OK` (confirma que o token funciona antes)
2. ADMIN desativa: `DELETE /usuarios/{id}` (o `id` do operador), header `{tokenAdmin}` → esperado `204 No Content`
3. Com o **mesmo** `{tokenOperador}` de antes (sem fazer login de novo), chame `GET /produtos` de novo → esperado **`403 Forbidden`**, não mais `200`

---

## TESTE 09 — Login do usuário desativado falha

**Configuração:** `POST /auth/login` com as credenciais do operador desativado.

**Resposta esperada:** `401 Unauthorized` — mesma mensagem genérica de credenciais inválidas (não revela que a conta existe mas está desativada).

---

## TESTE 10 — Usuário desativado some da listagem

**Configuração:** `GET /usuarios`, header `Authorization: Bearer {tokenAdmin}`.

**Resposta esperada:** `200 OK`, o operador desativado no Teste 08 **não aparece** mais no array (continua existindo no banco, só não aparece na listagem de ativos — mesmo padrão do soft delete de `Produto`).

---

## Resumo dos Endpoints Testados

| # | Método | URL | Auth | Status Esperado | Cenário |
|---|--------|-----|------|-----------------|---------|
| 01 | GET | /usuarios | ADMIN | 200 OK | Listar usuários ativos |
| 02 | POST | /auth/register | ADMIN | 201 Created | Cadastrar operador de teste |
| 03 | GET | /usuarios | OPERADOR | 403 Forbidden | Não-ADMIN não pode listar |
| 04 | PUT | /auth/me/senha | Qualquer | 204 No Content | Trocar a própria senha |
| 05 | POST | /auth/login | — | 401 / 200 | Confirmar troca de senha |
| 06 | PUT | /usuarios/{id}/senha | ADMIN | 204 No Content | ADMIN reseta senha de outro |
| 07 | DELETE | /usuarios/{id} | ADMIN (próprio id) | 409 Conflict | Bloquear autodesativação |
| 08 | DELETE | /usuarios/{id} | ADMIN | 204 → 403 | Token antigo perde acesso na hora |
| 09 | POST | /auth/login | — | 401 Unauthorized | Login de usuário desativado |
| 10 | GET | /usuarios | ADMIN | 200 OK | Desativado some da listagem |

---

## Mapa de campos dos DTOs

**PUT /auth/me/senha — TrocarSenhaDTO:**

| Campo | Tipo | Restrição |
|---|---|---|
| `novaSenha` | string | Obrigatório, mínimo 6 caracteres |

**PUT /usuarios/{id}/senha — ResetarSenhaDTO:** mesmo formato do `TrocarSenhaDTO`.

**Resposta de GET /usuarios e POST /auth/register — UsuarioResponseDTO:**

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | number | Identificador |
| `nome` / `email` | string | Dados do usuário |
| `cargo` | string | `ADMIN` ou `OPERADOR` |
| `ativo` | boolean | `false` = desativado (soft delete) — nunca aparece na listagem padrão |
