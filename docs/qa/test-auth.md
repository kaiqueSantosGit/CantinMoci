# Roteiro de Testes Manuais — Módulo Autenticação

> Execute os testes NA ORDEM apresentada. Cada teste pode depender dos dados/tokens criados pelo anterior.
> API local: http://localhost:8080 — API em produção: https://cantinmoci.onrender.com
> (troque a URL base conforme onde for testar; o resto é idêntico)

---

## Antes de começar

**Pré-requisito:** precisa existir pelo menos um usuário com cargo `ADMIN` no banco (o roteiro assume o usuário de teste criado manualmente via SQL na Fase 4 — ajuste o email/senha abaixo se o seu for diferente):
```
email: kaique@teste.com
senha: teste123
```

**Configuração do Postman:**
1. Crie uma Collection `CantinMoci - Auth`.
2. Para requests com body (POST): aba "Body" > "raw" > dropdown "JSON".
3. Para requests que exigem token: aba "Authorization" > Type: "Bearer Token" > cole o token no campo — ou, na aba "Headers", adicione manualmente `Authorization` = `Bearer SEU_TOKEN_AQUI`.

---

## TESTE 01 — Login com credenciais válidas

**O que valida:** o fluxo principal de autenticação — email e senha corretos retornam um token JWT.

**Configuração:**
- Método: `POST`
- URL: `http://localhost:8080/auth/login`

**Body JSON:**
```json
{
  "email": "kaique@teste.com",
  "senha": "teste123"
}
```

**Resposta esperada:**
- Status HTTP: `200 OK`
- Corpo:
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9...." }
```

**Anote esse token como `tokenAdmin`** — vai ser usado nos testes seguintes.

---

## TESTE 02 — Login com senha errada

**O que valida:** credenciais inválidas retornam `401 Unauthorized` — não `500` (bug corrigido em 2026-08-17: antes, uma `RuntimeException` genérica não tinha status HTTP mapeado; hoje usa `UnauthorizedException`, que tem `@ResponseStatus(UNAUTHORIZED)`).

**Configuração:**
- Método: `POST`
- URL: `http://localhost:8080/auth/login`

**Body JSON:**
```json
{
  "email": "kaique@teste.com",
  "senha": "senhaErrada"
}
```

**Resposta esperada:**
- Status HTTP: `401 Unauthorized`
- Corpo contém `"message": "Credenciais invalidas"`

---

## TESTE 03 — Login com email inexistente

**O que valida:** email que não existe no banco também retorna `401` com a **mesma mensagem genérica** do Teste 02 — de propósito, para não revelar quais emails estão cadastrados.

**Configuração:**
- Método: `POST`
- URL: `http://localhost:8080/auth/login`

**Body JSON:**
```json
{
  "email": "naoexiste@teste.com",
  "senha": "qualquercoisa"
}
```

**Resposta esperada:**
- Status HTTP: `401 Unauthorized`
- Mesma mensagem `"Credenciais invalidas"` do Teste 02 — confirme que é idêntica.

---

## TESTE 04 — Rota protegida sem token

**O que valida:** `/produtos` (e qualquer rota não listada como pública) recusa acesso sem token.

**Configuração:**
- Método: `GET`
- URL: `http://localhost:8080/produtos`
- Sem header de Authorization

**Resposta esperada:**
- Status HTTP: `403 Forbidden`

**Nota:** o esperado "tecnicamente correto" para "não autenticado" seria `401`, não `403` — mas como o projeto não configura um `AuthenticationEntryPoint` customizado, o Spring Security usa `403` como padrão nesse cenário. Não é um bug (é o comportamento padrão do framework), só vale documentar para não confundir com o Teste 02/03.

---

## TESTE 05 — Rota protegida com token válido

**O que valida:** com um token de `ADMIN` válido, `/produtos` responde normalmente.

**Configuração:**
- Método: `GET`
- URL: `http://localhost:8080/produtos`
- Header: `Authorization: Bearer {tokenAdmin}` (do Teste 01)

**Resposta esperada:**
- Status HTTP: `200 OK`
- Corpo: array de produtos (pode estar vazio `[]`, se não houver nenhum)

---

## TESTE 06 — Cadastrar novo usuário (ADMIN cadastrando um OPERADOR)

**O que valida:** o novo endpoint `POST /auth/register` cria um usuário quando chamado por um ADMIN autenticado, com a senha salva como hash (nunca em texto puro) e sem devolver a senha na resposta.

**Configuração:**
- Método: `POST`
- URL: `http://localhost:8080/auth/register`
- Header: `Authorization: Bearer {tokenAdmin}`

**Body JSON:**
```json
{
  "nome": "Maria Operadora",
  "email": "maria@cantinmoci.com",
  "senha": "senha123",
  "cargo": "OPERADOR"
}
```

**Resposta esperada:**
- Status HTTP: `201 Created`
- Corpo (sem o campo `senha`):
```json
{
  "id": 2,
  "nome": "Maria Operadora",
  "email": "maria@cantinmoci.com",
  "cargo": "OPERADOR"
}
```

**O que verificar:** o corpo NÃO contém o campo `senha`/`hash` em nenhum formato.

---

## TESTE 07 — Cadastrar com email já existente

**O que valida:** o sistema impede dois usuários com o mesmo email (`EmailJaCadastradoException`, HTTP 409).

**Configuração:** igual ao Teste 06, mesmo body (email `maria@cantinmoci.com` repetido).

**Resposta esperada:**
- Status HTTP: `409 Conflict`
- Corpo contém `"message": "Ja existe um usuario cadastrado com o email: maria@cantinmoci.com"`

---

## TESTE 08 — Cadastrar sem token

**O que valida:** `/auth/register` também exige autenticação — sem token, nem chega a validar o body.

**Configuração:**
- Método: `POST`
- URL: `http://localhost:8080/auth/register`
- Sem header de Authorization
- Body: qualquer um válido (ex: igual ao Teste 06, com email diferente)

**Resposta esperada:**
- Status HTTP: `403 Forbidden`

---

## TESTE 09 — Login com o usuário recém-criado

**O que valida:** o usuário OPERADOR criado no Teste 06 consegue logar normalmente — prova que o hash BCrypt gerado no cadastro bate com a senha original.

**Configuração:**
- Método: `POST`
- URL: `http://localhost:8080/auth/login`

**Body JSON:**
```json
{
  "email": "maria@cantinmoci.com",
  "senha": "senha123"
}
```

**Resposta esperada:**
- Status HTTP: `200 OK`
- Corpo com um token novo

**Anote esse token como `tokenOperador`.**

---

## TESTE 10 — Cadastrar com token de um OPERADOR (não-ADMIN)

**O que valida:** o cadastro é restrito a ADMIN — um OPERADOR autenticado, mas sem esse cargo, não pode criar novos usuários (`hasRole("ADMIN")` no `SecurityConfig`).

**Configuração:**
- Método: `POST`
- URL: `http://localhost:8080/auth/register`
- Header: `Authorization: Bearer {tokenOperador}` (do Teste 09)

**Body JSON:**
```json
{
  "nome": "Outro Usuario",
  "email": "outro@cantinmoci.com",
  "senha": "senha123",
  "cargo": "OPERADOR"
}
```

**Resposta esperada:**
- Status HTTP: `403 Forbidden`

**O que verificar:** mesmo com um token válido e uma sessão autenticada de verdade, a criação é recusada — confirma que a restrição é por **cargo**, não só por "estar logado".

---

## TESTE 11 — Cadastro com body inválido

**O que valida:** as validações do `RegisterRequestDTO` (`@NotBlank`, `@Size(min = 6)`) bloqueiam dados incompletos antes de tocar no banco.

**Configuração:**
- Método: `POST`
- URL: `http://localhost:8080/auth/register`
- Header: `Authorization: Bearer {tokenAdmin}`

**Body JSON (senha curta demais, cargo ausente):**
```json
{
  "nome": "",
  "email": "",
  "senha": "123",
  "cargo": null
}
```

**Resposta esperada:**
- Status HTTP: `400 Bad Request`

---

## Resumo dos Endpoints Testados

| # | Método | URL | Auth | Status Esperado | Cenário |
|---|--------|-----|------|-----------------|---------|
| 01 | POST | /auth/login | — | 200 OK | Login válido |
| 02 | POST | /auth/login | — | 401 Unauthorized | Senha errada |
| 03 | POST | /auth/login | — | 401 Unauthorized | Email inexistente |
| 04 | GET | /produtos | Sem token | 403 Forbidden | Rota protegida sem token |
| 05 | GET | /produtos | Token ADMIN | 200 OK | Rota protegida com token |
| 06 | POST | /auth/register | Token ADMIN | 201 Created | Cadastrar OPERADOR |
| 07 | POST | /auth/register | Token ADMIN | 409 Conflict | Email duplicado |
| 08 | POST | /auth/register | Sem token | 403 Forbidden | Cadastro sem token |
| 09 | POST | /auth/login | — | 200 OK | Login com usuário recém-criado |
| 10 | POST | /auth/register | Token OPERADOR | 403 Forbidden | Não-ADMIN tentando cadastrar |
| 11 | POST | /auth/register | Token ADMIN | 400 Bad Request | Body inválido |

---

## Mapa de campos dos DTOs

**POST /auth/login — LoginRequestDTO:**

| Campo | Tipo | Restrição |
|---|---|---|
| `email` | string | — |
| `senha` | string | — |

**POST /auth/register — RegisterRequestDTO** (exige token de ADMIN):

| Campo | Tipo | Restrição |
|---|---|---|
| `nome` | string | Obrigatório, não pode ser vazio |
| `email` | string | Obrigatório, precisa ser único |
| `senha` | string | Obrigatório, mínimo 6 caracteres |
| `cargo` | string | Obrigatório — `"ADMIN"` ou `"OPERADOR"` |

**Resposta de POST /auth/register — UsuarioResponseDTO:**

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | number | Gerado automaticamente pelo banco |
| `nome` | string | Nome do usuário |
| `email` | string | Email (login) |
| `cargo` | string | `ADMIN` ou `OPERADOR` — nunca inclui a senha/hash |
