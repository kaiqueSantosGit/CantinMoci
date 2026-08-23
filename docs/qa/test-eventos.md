# Roteiro de Testes Manuais — Módulo Eventos

> Execute os testes NA ORDEM apresentada. Cada teste depende dos dados criados pelo anterior.
> API local: http://localhost:8080 — API em produção: https://cantinmoci.onrender.com
> Todas as rotas exigem token (qualquer usuário autenticado, ADMIN ou OPERADOR).
> **Faça este roteiro antes do `test-vendas.md`** — desde a Fase 6, vendas exigem um evento aberto.

---

## Antes de começar

1. Faça login e guarde o token.
2. Anote o ID de um produto ativo (`GET /produtos`) — os exemplos usam produto id `2`.

---

## TESTE 01 — Tentar abrir uma venda sem nenhum evento aberto

**O que valida:** o sistema recusa vendas sem um evento em andamento.

**Configuração:** `POST /vendas`, sem body.

**Resposta esperada:** `409 Conflict`, mensagem `"Nenhum evento esta aberto no momento..."`.

---

## TESTE 02 — Criar (abrir) um evento

**Configuração:** `POST /eventos`
```json
{ "nome": "Festa Junina 2026", "local": "Salão Paroquial" }
```

**Resposta esperada:** `201 Created`
```json
{
  "id": 1,
  "nome": "Festa Junina 2026",
  "local": "Salão Paroquial",
  "status": "ABERTO",
  "dataAbertura": "...",
  "dataEncerramento": null
}
```
**Anote o `id`.**

---

## TESTE 03 — Tentar criar outro evento enquanto o primeiro está aberto

**O que valida:** só pode existir um evento `ABERTO` por vez.

**Configuração:** `POST /eventos` de novo, com qualquer nome.

**Resposta esperada:** `409 Conflict`, mensagem informando o nome do evento já aberto.

---

## TESTE 04 — Alocar estoque de um produto para o evento

**Configuração:** `POST /eventos/{id}/produtos`
```json
{ "produtoId": 2, "quantidade": 20 }
```

**Resposta esperada:** `201 Created`
```json
{ "produtoId": 2, "nomeProduto": "...", "quantidadeInicial": 20, "quantidadeAtual": 20 }
```

---

## TESTE 05 — Reforçar o estoque do mesmo produto

**O que valida:** chamar o endpoint de novo para o mesmo produto **soma** a quantidade, não substitui.

**Configuração:** `POST /eventos/{id}/produtos` de novo, mesmo `produtoId`, quantidade `5`.

**Resposta esperada:** `201 Created`, `quantidadeInicial` e `quantidadeAtual` = `25` (20 + 5).

---

## TESTE 06 — Listar produtos do evento

**Configuração:** `GET /eventos/{id}/produtos`

**Resposta esperada:** `200 OK`, array com o produto alocado e o estoque atual.

---

## TESTE 07 — Abrir uma venda (agora com evento aberto)

**O que valida:** a venda é vinculada **automaticamente** ao evento aberto — sem informar nada no body.

**Configuração:** `POST /vendas`, sem body.

**Resposta esperada:** `201 Created`, com `eventoId` e `nomeEvento` preenchidos igual ao evento do Teste 02.

> A partir daqui, siga o `docs/qa/test-vendas.md` a partir do Teste 02 (adicionar item, finalizar, etc.) usando essa venda e esse produto — a lógica de estoque agora consulta o que foi alocado no Teste 04/05, não mais `Produto.quantidadeEmEstoque`.

---

## TESTE 08 — Relatório do evento (após pelo menos uma venda finalizada)

**Pré-requisito:** finalize a venda do Teste 07 (ver `test-vendas.md`).

**Configuração:** `GET /eventos/{id}/relatorio`

**Resposta esperada:** `200 OK`
```json
{
  "eventoId": 1,
  "nomeEvento": "Festa Junina 2026",
  "valorTotalArrecadado": 36.00,
  "quantidadeVendas": 1,
  "ticketMedio": 36.00,
  "produtosMaisVendidos": [
    { "produtoId": 2, "nomeProduto": "...", "quantidadeVendida": 8, "valorArrecadado": 36.00 }
  ]
}
```
**O que verificar:** os valores batem com a(s) venda(s) finalizada(s). Vendas ainda `ABERTA` (carrinho em andamento) **não entram** na contabilidade.

---

## TESTE 09 — Tentar encerrar o evento com uma venda ainda aberta

**Configuração:** abra outra venda (`POST /vendas`) e, **sem finalizá-la**, tente `POST /eventos/{id}/encerrar`.

**Resposta esperada:** `409 Conflict`, mensagem informando quantas vendas em aberto existem.

---

## TESTE 10 — Encerrar o evento

**Pré-requisito:** finalize (ou não deixe nenhuma) venda `ABERTA` pendente nesse evento.

**Configuração:** `POST /eventos/{id}/encerrar`

**Resposta esperada:** `200 OK`, `status: "ENCERRADO"`, `dataEncerramento` preenchida.

---

## TESTE 11 — Tentar alocar estoque num evento encerrado

**Configuração:** `POST /eventos/{id}/produtos` no evento já encerrado.

**Resposta esperada:** `409 Conflict`.

---

## TESTE 12 — Tentar abrir venda sem evento aberto (de novo)

**O que valida:** depois de encerrar, o sistema volta a recusar vendas novas até que outro evento seja aberto — mesmo comportamento do Teste 01.

**Configuração:** `POST /vendas`.

**Resposta esperada:** `409 Conflict`.

---

## Resumo dos Endpoints Testados

| # | Método | URL | Status Esperado | Cenário |
|---|--------|-----|-----------------|---------|
| 01 | POST | /vendas | 409 Conflict | Sem evento aberto |
| 02 | POST | /eventos | 201 Created | Criar/abrir evento |
| 03 | POST | /eventos | 409 Conflict | Já existe evento aberto |
| 04 | POST | /eventos/{id}/produtos | 201 Created | Alocar estoque |
| 05 | POST | /eventos/{id}/produtos | 201 Created | Reforçar estoque (soma) |
| 06 | GET | /eventos/{id}/produtos | 200 OK | Listar produtos do evento |
| 07 | POST | /vendas | 201 Created | Venda vinculada automaticamente |
| 08 | GET | /eventos/{id}/relatorio | 200 OK | Relatório com venda finalizada |
| 09 | POST | /eventos/{id}/encerrar | 409 Conflict | Venda aberta bloqueia encerramento |
| 10 | POST | /eventos/{id}/encerrar | 200 OK | Encerrar evento |
| 11 | POST | /eventos/{id}/produtos | 409 Conflict | Alocar em evento encerrado |
| 12 | POST | /vendas | 409 Conflict | Sem evento aberto (de novo) |

---

## Mapa de campos dos DTOs

**POST /eventos — EventoRequestDTO:**

| Campo | Tipo | Restrição |
|---|---|---|
| `nome` | string | Obrigatório |
| `local` | string | Opcional |

**POST /eventos/{id}/produtos — AlocarEstoqueEventoDTO:**

| Campo | Tipo | Restrição |
|---|---|---|
| `produtoId` | number | Obrigatório |
| `quantidade` | number | Obrigatório, mínimo 1 — soma ao que já existir |

**Resposta de evento — EventoResponseDTO:** `id`, `nome`, `local`, `status` (`ABERTO`/`ENCERRADO`), `dataAbertura`, `dataEncerramento`.

**Resposta de estoque do evento — EstoqueEventoResponseDTO:** `produtoId`, `nomeProduto`, `quantidadeInicial` (total já alocado), `quantidadeAtual` (o que resta).

**Resposta de relatório — RelatorioEventoDTO:** `eventoId`, `nomeEvento`, `valorTotalArrecadado`, `quantidadeVendas`, `ticketMedio`, `produtosMaisVendidos` (array de `{produtoId, nomeProduto, quantidadeVendida, valorArrecadado}`, ordenado do mais vendido para o menos vendido).
