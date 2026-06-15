# Roteiro de Testes Manuais — Módulo Produto

> Execute os testes NA ORDEM apresentada. Cada teste pode depender dos dados criados pelo anterior.
> API rodando em: http://localhost:8080

---

## Antes de começar — Configuração inicial do Postman

1. Abra o Postman.
2. Crie uma nova Collection clicando em "Collections" no painel esquerdo, depois no botão "+".
3. Nomeie a collection como `CantinMoci - Produto`.
4. Para cada teste abaixo, clique no botão "+" dentro da collection para criar uma nova request.

**Como configurar o Content-Type (obrigatório para POST e PUT):**
- Após criar a request, clique na aba "Headers".
- Adicione a chave `Content-Type` com o valor `application/json`.
- Alternativamente, use a aba "Body" > selecione "raw" > e no dropdown à direita selecione "JSON" — o Postman adiciona o header automaticamente.

---

## TESTE 01 — Criar produto válido (Coxinha)

**O que este teste valida:** A rota POST cria um produto novo no banco, gera um ID automático e retorna o produto criado com status 201.

**Configuração no Postman:**
- Clique em "+" para nova request.
- Selecione o método: `POST`
- URL: `http://localhost:8080/produtos`
- Vá na aba "Body" > selecione "raw" > dropdown para "JSON"
- Cole o body abaixo no campo de texto

**Body JSON:**
```json
{
  "nome": "Coxinha",
  "preco": 5.00,
  "quantidadeEmEstoque": 30
}
```

**Como enviar:** Clique no botão "Send".

**Resposta esperada:**
- Status HTTP: `201 Created`
- Corpo JSON:
```json
{
  "id": 1,
  "nome": "Coxinha",
  "preco": 5.00,
  "quantidadeEmEstoque": 30,
  "ativo": true
}
```

**Anote o valor do campo `"id"` retornado** — você vai precisar dele nos testes seguintes. Se o banco estiver limpo, o ID sera 1.

---

## TESTE 02 — Criar segundo produto (Suco de Laranja)

**O que este teste valida:** É possível criar múltiplos produtos. Cada um recebe um ID único e incremental.

**Configuração no Postman:**
- Método: `POST`
- URL: `http://localhost:8080/produtos`
- Body > raw > JSON

**Body JSON:**
```json
{
  "nome": "Suco de Laranja",
  "preco": 4.50,
  "quantidadeEmEstoque": 20
}
```

**Resposta esperada:**
- Status HTTP: `201 Created`
- Corpo JSON:
```json
{
  "id": 2,
  "nome": "Suco de Laranja",
  "preco": 4.50,
  "quantidadeEmEstoque": 20,
  "ativo": true
}
```

**Anote este ID também** (deve ser 2 se for banco limpo).

---

## TESTE 03 — Listar todos os produtos ativos

**O que este teste valida:** A rota GET /produtos retorna apenas produtos com `ativo = true`. Ambos os produtos criados devem aparecer.

**Configuração no Postman:**
- Método: `GET`
- URL: `http://localhost:8080/produtos`
- Não é necessário body (GET não envia body)

**Resposta esperada:**
- Status HTTP: `200 OK`
- Corpo JSON (array com os 2 produtos):
```json
[
  {
    "id": 1,
    "nome": "Coxinha",
    "preco": 5.00,
    "quantidadeEmEstoque": 30,
    "ativo": true
  },
  {
    "id": 2,
    "nome": "Suco de Laranja",
    "preco": 4.50,
    "quantidadeEmEstoque": 20,
    "ativo": true
  }
]
```

**O que verificar:** Dois produtos no array, ambos com `"ativo": true`.

---

## TESTE 04 — Buscar produto por ID

**O que este teste valida:** A rota GET /produtos/{id} retorna um único produto pelo seu identificador.

**Configuração no Postman:**
- Método: `GET`
- URL: `http://localhost:8080/produtos/1`
- Nenhum body necessário

**Resposta esperada:**
- Status HTTP: `200 OK`
- Corpo JSON:
```json
{
  "id": 1,
  "nome": "Coxinha",
  "preco": 5.00,
  "quantidadeEmEstoque": 30,
  "ativo": true
}
```

**O que verificar:** Apenas o produto de ID 1 é retornado, não um array — objeto único.

---

## TESTE 05 — Atualizar produto (PUT)

**O que este teste valida:** A rota PUT atualiza os dados de um produto existente. O ID e o campo `ativo` permanecem intactos. Apenas nome, preço e estoque são substituídos.

**Configuração no Postman:**
- Método: `PUT`
- URL: `http://localhost:8080/produtos/1`
- Body > raw > JSON

**Body JSON:**
```json
{
  "nome": "Coxinha",
  "preco": 6.00,
  "quantidadeEmEstoque": 25
}
```

**Resposta esperada:**
- Status HTTP: `200 OK`
- Corpo JSON:
```json
{
  "id": 1,
  "nome": "Coxinha",
  "preco": 6.00,
  "quantidadeEmEstoque": 25,
  "ativo": true
}
```

**O que verificar:** O preço mudou de 5.00 para 6.00, estoque de 30 para 25. O campo `"ativo"` continua `true` — o PUT não toca nesse campo.

---

## TESTE 06 — Soft delete (desativar produto)

**O que este teste valida:** A rota DELETE não apaga o registro do banco. Ela muda o campo `ativo` para `false`. A resposta não tem corpo (204 No Content é a convenção REST para delete bem-sucedido).

**Configuração no Postman:**
- Método: `DELETE`
- URL: `http://localhost:8080/produtos/1`
- Nenhum body necessário

**Resposta esperada:**
- Status HTTP: `204 No Content`
- Corpo: **vazio** (isso é correto — não é um erro)

**O que verificar:** O status é 204. O campo "Body" no Postman aparece em branco. Isso confirma que o produto foi desativado com sucesso.

---

## TESTE 07 — Listar produtos após o soft delete

**O que este teste valida:** Após desativar a Coxinha, a listagem de ativos deve retornar apenas o Suco de Laranja. A Coxinha ainda existe no banco, mas não aparece nesta rota porque tem `ativo = false`.

**Configuração no Postman:**
- Método: `GET`
- URL: `http://localhost:8080/produtos`
- Nenhum body necessário

**Resposta esperada:**
- Status HTTP: `200 OK`
- Corpo JSON (array com apenas 1 produto):
```json
[
  {
    "id": 2,
    "nome": "Suco de Laranja",
    "preco": 4.50,
    "quantidadeEmEstoque": 20,
    "ativo": true
  }
]
```

**O que verificar:** Apenas 1 item no array. A Coxinha (id 1) não aparece. Isso prova que o soft delete está funcionando corretamente.

---

## TESTE 08 — Buscar produto desativado por ID

**O que este teste valida:** A rota GET /produtos/{id} busca pelo ID diretamente no banco — independente de `ativo`. Isso confirma que o registro ainda existe, apenas está invisível na listagem geral.

**Configuração no Postman:**
- Método: `GET`
- URL: `http://localhost:8080/produtos/1`
- Nenhum body necessário

**Resposta esperada:**
- Status HTTP: `200 OK`
- Corpo JSON:
```json
{
  "id": 1,
  "nome": "Coxinha",
  "preco": 6.00,
  "quantidadeEmEstoque": 25,
  "ativo": false
}
```

**O que verificar:** O produto retorna normalmente, mas agora `"ativo": false`. Isso demonstra o soft delete — o dado é preservado, apenas sua visibilidade na listagem muda. O preço (6.00) e estoque (25) refletem a atualização do Teste 05.

---

## TESTE 09 — Validação de body inválido (POST com `{}`)

**O que este teste valida:** A API rejeita dados inválidos antes mesmo de chegar ao banco. O Spring Boot verifica as anotações `@NotBlank`, `@NotNull` e retorna 400 Bad Request com os erros de validação.

**Configuração no Postman:**
- Método: `POST`
- URL: `http://localhost:8080/produtos`
- Body > raw > JSON

**Body JSON (intencionalmente vazio):**
```json
{}
```

**Resposta esperada:**
- Status HTTP: `400 Bad Request`
- Corpo JSON (o Spring Boot retorna algo parecido com isto):
```json
{
  "timestamp": "...",
  "status": 400,
  "errors": [
    "O nome do produto e obrigatorio",
    "O preco e obrigatorio",
    "A quantidade em estoque e obrigatoria"
  ]
}
```

**Nota importante:** O formato exato do corpo de erro 400 depende de como o tratamento global de exceções foi configurado no projeto. Se não houver um `@ControllerAdvice` ainda, o Spring pode retornar o seu formato padrão com o campo `"errors"` ou `"message"`. O que importa validar é o status `400`.

**O que verificar:** O status deve ser `400 Bad Request`. Nenhum produto deve ser criado no banco.

---

## TESTE 10 — Buscar ID inexistente (404)

**O que este teste valida:** Quando o ID solicitado não existe no banco, a API retorna 404 Not Found com uma mensagem clara. Isso vem da exceção `ResourceNotFoundException` configurada no projeto.

**Configuração no Postman:**
- Método: `GET`
- URL: `http://localhost:8080/produtos/999`
- Nenhum body necessário

**Resposta esperada:**
- Status HTTP: `404 Not Found`
- Corpo JSON (formato padrão do Spring):
```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "Produto nao encontrado com id: 999",
  "path": "/produtos/999"
}
```

**O que verificar:** Status `404` e a mensagem `"Produto nao encontrado com id: 999"`. Confirma que a exceção `ResourceNotFoundException` está funcionando corretamente.

---

## Resumo dos Endpoints Testados

| # | Método | URL | Status Esperado | Cenário |
|---|--------|-----|-----------------|---------|
| 01 | POST | /produtos | 201 Created | Criar produto válido |
| 02 | POST | /produtos | 201 Created | Criar segundo produto |
| 03 | GET | /produtos | 200 OK | Listar todos os ativos |
| 04 | GET | /produtos/1 | 200 OK | Buscar por ID existente |
| 05 | PUT | /produtos/1 | 200 OK | Atualizar preço e estoque |
| 06 | DELETE | /produtos/1 | 204 No Content | Soft delete |
| 07 | GET | /produtos | 200 OK | Listar após delete (só 1 produto) |
| 08 | GET | /produtos/1 | 200 OK | Buscar produto desativado |
| 09 | POST | /produtos | 400 Bad Request | Body vazio — validação |
| 10 | GET | /produtos/999 | 404 Not Found | ID inexistente |

---

## Mapa de campos do DTO

**Campos obrigatórios no body (POST e PUT):**

| Campo JSON | Tipo | Restrição |
|---|---|---|
| `nome` | string | Obrigatório, não pode ser vazio ou só espaços |
| `preco` | number | Obrigatório, não pode ser negativo |
| `quantidadeEmEstoque` | integer | Obrigatório, não pode ser negativo |

**Campos retornados na resposta:**

| Campo JSON | Tipo | Descrição |
|---|---|---|
| `id` | number | Gerado automaticamente pelo banco |
| `nome` | string | Nome do produto |
| `preco` | number | Preço unitário |
| `quantidadeEmEstoque` | integer | Unidades disponíveis |
| `ativo` | boolean | true = ativo, false = desativado (soft delete) |
