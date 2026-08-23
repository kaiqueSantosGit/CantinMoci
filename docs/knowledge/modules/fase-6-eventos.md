# Fase 6 — Módulo Eventos

Tags: #modulo #concluido #eventos #estoque

Status: ✅ Concluída (2026-08-23) — testada localmente, 19 cenários

---

## Decisões de negócio (definidas antes de codar)

- **Estoque separado por evento** — não é mais só `Produto.quantidadeEmEstoque` (Fase 2/5); nova entidade `EstoqueEvento`
- **Vendas exigem um evento em andamento** — sem evento `ABERTO`, `POST /vendas` recusa
- **1 evento `ABERTO` por vez, vínculo automático** — a venda nova já nasce vinculada a ele, sem o operador escolher
- **Relatório elaborado**: total arrecadado, quantidade de vendas, ticket médio, ranking de produtos mais vendidos
- Sistema continua **single-tenant** — multi-tenant vira [[fase-9-multi-tenant]] (ver [[multi-tenant]]), só depois do sistema validado em uso real

## O que foi implementado

- Enum `StatusEvento` (ABERTO, ENCERRADO)
- Entidade `Evento`: nome, local, status, dataAbertura/dataEncerramento
- Entidade `EstoqueEvento`: ponte Evento↔Produto — `quantidadeInicial` (total alocado) e `quantidadeAtual` (o que resta), com `@Version` (lock otimista, substitui o do `Produto` na Fase 5 pra esse fluxo)
- `Venda` ganha campo `evento` (`nullable` — vendas anteriores à Fase 6 continuam válidas sem evento)
- **`VendaService` refatorado**: `abrirVenda()` busca o evento `ABERTO` sozinho (`EventoRepository.findFirstByStatus`) e recusa se não houver nenhum; validação/desconto de estoque em `adicionarItem`/`atualizarQuantidade`/`finalizar` passa a consultar `EstoqueEvento`, não mais `Produto` diretamente
- `EventoService`: criar (recusa se já existe outro `ABERTO`), encerrar (recusa se há vendas `ABERTA` pendentes no evento), alocar/reforçar estoque (soma, não substitui), listar produtos do evento, gerar relatório
- Relatório calculado em memória (sem query SQL de agregação) — soma vendas `FINALIZADA` do evento, agrupa itens por produto via `Map<Long, ...>` chaveado pelo ID (evita depender de `equals`/`hashCode` de entidade JPA)

## Endpoints implementados

| Método | Rota | Descrição |
|---|---|---|
| POST | /eventos | Cria e abre um evento (falha se já existe um aberto) |
| POST | /eventos/{id}/encerrar | Encerra o evento (falha se há vendas abertas) |
| GET | /eventos | Lista eventos |
| GET | /eventos/{id} | Consulta um evento |
| POST | /eventos/{id}/produtos | Aloca (soma) estoque de um produto pro evento |
| GET | /eventos/{id}/produtos | Lista produtos + estoque disponível no evento |
| GET | /eventos/{id}/relatorio | Total arrecadado, qtd vendas, ticket médio, produtos mais vendidos |

## Arquivos principais

- `backend/cantinmoci/src/main/java/com/cantinmoci/model/Evento.java`, `StatusEvento.java`, `EstoqueEvento.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/service/EventoService.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/controller/EventoController.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/repository/EventoRepository.java`, `EstoqueEventoRepository.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/service/VendaService.java` (refatorado)

## Roteiro de testes

- Arquivo: `docs/qa/test-eventos.md` (12 testes) + `docs/qa/test-vendas.md` atualizado (o fluxo de vendas agora depende de um evento aberto e estoque alocado)
- Cenários cobertos: evento único aberto, reforço de estoque (soma), venda vinculada automaticamente, estoque insuficiente no evento, relatório com valores conferidos manualmente, encerramento bloqueado por venda `ABERTA` pendente, tudo trava depois de encerrado (nem aloca estoque, nem abre venda nova)

## Relacionado

- [[fase-5-vendas]]
- [[fase-9-multi-tenant]]
- [[multi-tenant]]
- [[arquitetura]]
- [[JPA]]
