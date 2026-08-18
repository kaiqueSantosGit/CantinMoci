# Fase 5 — Módulo Vendas + Estoque

Tags: #modulo #concluido #vendas #estoque #concorrencia

Status: ✅ Concluída (2026-08-17) — testada localmente, 14 cenários

---

## Decisões de negócio (definidas antes de codar)

- **Estoque insuficiente:** bloqueia a venda, mensagem informa a quantidade disponível para o operador ajustar
- **Modelo de venda:** carrinho persistido — a própria `Venda` nasce `ABERTA` e vira `FINALIZADA` ao ser fechada (não existe entidade `Carrinho` separada)
- **Cancelamento/estorno:** fora do escopo desta fase (só criar e consultar)
- **Concorrência no estoque:** lock otimista via `@Version` no `Produto`

## O que foi implementado

- Enum `StatusVenda` (ABERTA, FINALIZADA)
- Entidade `Venda`: status, usuário (operador logado via `@AuthenticationPrincipal`), valorTotal (recalculado a cada mudança), dataAbertura/dataFinalizacao, itens (`@OneToMany` cascade ALL + orphanRemoval)
- Entidade `ItemVenda`: produto, quantidade, precoUnitario — **snapshot** do preço no momento da venda (não muda se o preço do produto mudar depois)
- Campo `@Version` adicionado ao `Produto` já existente (Fase 2)
- `VendaService`: abrir, adicionar/atualizar/remover item (com validação de estoque e de produto ativo), buscar, listar (filtro por status), finalizar (`@Transactional`, revalida estoque de todos os itens, desconta, captura `ObjectOptimisticLockingFailureException`)
- Exceções: `EstoqueInsuficienteException` (400), `OperacaoInvalidaException` (409), `VendaConcorrenteException` (409)

## Endpoints implementados

| Método | Rota | Descrição |
|---|---|---|
| POST | /vendas | Abre um carrinho novo |
| POST | /vendas/{id}/itens | Adiciona produto ao carrinho |
| PUT | /vendas/{id}/itens/{itemId} | Ajusta quantidade de um item |
| DELETE | /vendas/{id}/itens/{itemId} | Remove item do carrinho |
| GET | /vendas/{id} | Consulta uma venda |
| GET | /vendas | Lista vendas (filtro opcional `?status=`) |
| POST | /vendas/{id}/finalizar | Fecha o carrinho, desconta estoque |

## Arquivos principais

- `backend/cantinmoci/src/main/java/com/cantinmoci/model/Venda.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/model/ItemVenda.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/model/StatusVenda.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/service/VendaService.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/controller/VendaController.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/repository/VendaRepository.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/exception/EstoqueInsuficienteException.java`, `OperacaoInvalidaException.java`, `VendaConcorrenteException.java`

## Roteiro de testes

- Arquivo: `docs/qa/test-vendas.md`
- 14 testes: carrinho (abrir/adicionar/ajustar/remover), finalização, baixa de estoque, produto desativado, venda já finalizada, carrinho vazio, listagem filtrada
- Concorrência real (duas requisições simultâneas) não coberta por teste manual — anotado no backlog como melhoria de teste automatizado

## Bug de migração encontrado e corrigido

`ddl-auto=update` tentou `ALTER TABLE produtos ADD COLUMN version bigint NOT NULL` sem valor padrão, o que falha em qualquer tabela com linhas já existentes (aconteceu no banco local, que já tinha produtos da Fase 2). Corrigido com uma migração manual (`ALTER TABLE ... ADD COLUMN version bigint NOT NULL DEFAULT 0`) antes de subir a aplicação. Produção (Neon) não foi afetada porque `produtos` lá ainda estava vazia — mas fica registrado como cuidado para a próxima coluna `NOT NULL` adicionada a uma tabela com dados.

## Relacionado

- [[fase-2-produto]]
- [[fase-6-eventos]]
- [[arquitetura]]
- [[JPA]]
