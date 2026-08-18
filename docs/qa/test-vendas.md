# Roteiro de Testes Manuais — Módulo Vendas + Estoque

> Execute os testes NA ORDEM apresentada. Cada teste depende dos dados criados pelo anterior.
> API local: http://localhost:8080 — API em produção: https://cantinmoci.onrender.com
> Todas as rotas de `/vendas` exigem token (qualquer usuário autenticado, ADMIN ou OPERADOR).

---

## Antes de começar

1. Faça login (`POST /auth/login`) com um usuário existente e guarde o token — todos os testes abaixo usam `Authorization: Bearer {token}`.
2. Anote o ID de um produto **ativo** e com estoque > 0 (`GET /produtos`) — os exemplos abaixo usam produto id `2`, preço 4.50, estoque 20.
3. Se possível, anote também o ID de um produto **desativado** (`ativo: false`) para o Teste 12.

---

## TESTE 01 — Abrir uma venda (carrinho)

**O que valida:** `POST /vendas` cria uma venda com status `ABERTA`, vazia, vinculada ao usuário do token — sem precisar informar o usuário no body.

**Configuração:** `POST /vendas`, sem body.

**Resposta esperada:** `201 Created`
```json
{
  "id": 1,
  "status": "ABERTA",
  "usuarioId": 1,
  "nomeUsuario": "...",
  "itens": [],
  "valorTotal": 0,
  "dataAbertura": "...",
  "dataFinalizacao": null
}
```
**Anote o `id`** — vai ser usado em quase todos os testes seguintes.

---

## TESTE 02 — Adicionar item ao carrinho

**O que valida:** o item é adicionado com o **preço atual do produto** (snapshot) e o `valorTotal` da venda é recalculado automaticamente.

**Configuração:** `POST /vendas/{id}/itens`
```json
{ "produtoId": 2, "quantidade": 3 }
```

**Resposta esperada:** `201 Created`, `itens` com 1 elemento, `valorTotal` = preço × 3.

---

## TESTE 03 — Adicionar item com estoque insuficiente

**O que valida:** o sistema recusa a quantidade e informa quanto há disponível — o operador pode ajustar e tentar de novo.

**Configuração:** mesmo produto do Teste 02, quantidade muito maior que o estoque (ex: `999`).

**Resposta esperada:** `400 Bad Request`, mensagem tipo `"Estoque insuficiente para 'X': disponivel Y, solicitado 999"`.

---

## TESTE 04 — Ajustar a quantidade de um item

**Configuração:** `PUT /vendas/{id}/itens/{itemId}`
```json
{ "quantidade": 5 }
```

**Resposta esperada:** `200 OK`, `valorTotal` recalculado com a nova quantidade.

---

## TESTE 05 — Adicionar um segundo item

**Configuração:** `POST /vendas/{id}/itens` com outro `produtoId`.

**Resposta esperada:** `201 Created`, `itens` agora com 2 elementos, `valorTotal` = soma dos dois subtotais.

---

## TESTE 06 — Remover um item do carrinho

**Configuração:** `DELETE /vendas/{id}/itens/{itemId}` (o item adicionado no Teste 05).

**Resposta esperada:** `200 OK`, `itens` volta a ter só 1 elemento, `valorTotal` recalculado.

---

## TESTE 07 — Consultar o estoque ANTES de finalizar

**O que valida:** o estoque só é descontado na finalização — montar/ajustar o carrinho não mexe no estoque.

**Configuração:** `GET /produtos/{id}` (o produto usado no Teste 02/04).

**Resposta esperada:** `200 OK`, `quantidadeEmEstoque` igual ao valor original (ainda não descontado).

---

## TESTE 08 — Finalizar a venda

**Configuração:** `POST /vendas/{id}/finalizar`, sem body.

**Resposta esperada:** `200 OK`, `status: "FINALIZADA"`, `dataFinalizacao` preenchida.

---

## TESTE 09 — Confirmar a baixa de estoque

**Configuração:** `GET /produtos/{id}` (mesmo produto do Teste 07).

**Resposta esperada:** `200 OK`, `quantidadeEmEstoque` reduzido exatamente na quantidade vendida.

---

## TESTE 10 — Tentar alterar uma venda já finalizada

**Configuração:** `POST /vendas/{id}/itens` na mesma venda do Teste 08.

**Resposta esperada:** `409 Conflict`, mensagem `"Esta venda ja foi finalizada e nao pode mais ser alterada"`.

---

## TESTE 11 — Tentar finalizar a mesma venda de novo

**Configuração:** `POST /vendas/{id}/finalizar` de novo, na venda já finalizada.

**Resposta esperada:** `409 Conflict`.

---

## TESTE 12 — Vender um produto desativado

**O que valida:** produtos com soft delete (`ativo: false`) não podem ser vendidos.

**Configuração:** abra uma venda nova (`POST /vendas`), depois `POST /vendas/{novaId}/itens` com o `produtoId` de um produto desativado.

**Resposta esperada:** `409 Conflict`, mensagem `"Produto 'X' esta desativado e nao pode ser vendido"`.

---

## TESTE 13 — Finalizar um carrinho vazio

**Configuração:** abra outra venda nova (`POST /vendas`) e finalize (`POST /vendas/{id}/finalizar`) sem adicionar nenhum item.

**Resposta esperada:** `409 Conflict`, mensagem `"Nao e possivel finalizar uma venda sem nenhum item"`.

---

## TESTE 14 — Listar histórico (só vendas finalizadas)

**Configuração:** `GET /vendas?status=FINALIZADA`

**Resposta esperada:** `200 OK`, array contendo só as vendas com `status: "FINALIZADA"` (as abertas do Teste 12/13 não aparecem).

---

## Nota sobre concorrência (não coberto por este roteiro manual)

O lock otimista (`@Version` no `Produto`) protege contra duas vendas finalizando ao mesmo tempo e descontando o mesmo estoque de forma inconsistente. Esse cenário exige duas requisições **simultâneas** de verdade — não é possível reproduzir de forma confiável testando manualmente uma requisição de cada vez no Postman. O mecanismo foi validado por revisão de código; um teste automatizado (JUnit, com duas threads) seria a forma correta de cobrir esse caso — fica registrado no [backlog](../backlog.md) como melhoria futura de testes.

---

## Resumo dos Endpoints Testados

| # | Método | URL | Status Esperado | Cenário |
|---|--------|-----|-----------------|---------|
| 01 | POST | /vendas | 201 Created | Abrir carrinho |
| 02 | POST | /vendas/{id}/itens | 201 Created | Adicionar item válido |
| 03 | POST | /vendas/{id}/itens | 400 Bad Request | Estoque insuficiente |
| 04 | PUT | /vendas/{id}/itens/{itemId} | 200 OK | Ajustar quantidade |
| 05 | POST | /vendas/{id}/itens | 201 Created | Adicionar segundo item |
| 06 | DELETE | /vendas/{id}/itens/{itemId} | 200 OK | Remover item |
| 07 | GET | /produtos/{id} | 200 OK | Estoque intacto antes de finalizar |
| 08 | POST | /vendas/{id}/finalizar | 200 OK | Finalizar venda |
| 09 | GET | /produtos/{id} | 200 OK | Estoque descontado |
| 10 | POST | /vendas/{id}/itens | 409 Conflict | Alterar venda finalizada |
| 11 | POST | /vendas/{id}/finalizar | 409 Conflict | Finalizar de novo |
| 12 | POST | /vendas/{id}/itens | 409 Conflict | Vender produto desativado |
| 13 | POST | /vendas/{id}/finalizar | 409 Conflict | Finalizar carrinho vazio |
| 14 | GET | /vendas?status=FINALIZADA | 200 OK | Histórico filtrado |

---

## Mapa de campos dos DTOs

**POST /vendas/{id}/itens — ItemVendaRequestDTO:**

| Campo | Tipo | Restrição |
|---|---|---|
| `produtoId` | number | Obrigatório |
| `quantidade` | number | Obrigatório, mínimo 1 |

**PUT /vendas/{id}/itens/{itemId} — AtualizarQuantidadeItemDTO:**

| Campo | Tipo | Restrição |
|---|---|---|
| `quantidade` | number | Obrigatório, mínimo 1 |

**Resposta de qualquer endpoint de venda — VendaResponseDTO:**

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | number | Identificador da venda |
| `status` | string | `ABERTA` ou `FINALIZADA` |
| `usuarioId` / `nomeUsuario` | number / string | Operador que abriu a venda |
| `itens` | array | Lista de `ItemVendaResponseDTO` |
| `valorTotal` | number | Soma dos subtotais |
| `dataAbertura` / `dataFinalizacao` | datetime | Timestamps |

**Cada item em `itens` — ItemVendaResponseDTO:**

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | number | Identificador do item |
| `produtoId` / `nomeProduto` | number / string | Produto vendido |
| `quantidade` | number | Unidades |
| `precoUnitario` | number | Preço no momento em que foi adicionado (snapshot) |
| `subtotal` | number | `precoUnitario × quantidade` |
