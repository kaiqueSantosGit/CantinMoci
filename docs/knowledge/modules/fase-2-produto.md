# Fase 2 — Módulo Produto (CRUD)

Tags: #modulo #em-andamento #crud #produto

Status: ⏳ Em andamento — backend implementado, testes aguardando execução

---

## O que foi implementado

- Entidade `Produto` com campos: id, nome, preco, quantidadeEmEstoque, ativo
- Soft delete via campo `ativo` (Boolean, padrão true)
- Camadas: Model → Repository → Service → Controller → DTOs → Exception
- Validação de entrada via Bean Validation (`@NotBlank`, `@NotNull`, `@DecimalMin`, `@Min`)

## Endpoints implementados

| Método | Rota | Retorno sucesso | Retorno erro |
|---|---|---|---|
| GET | /produtos | 200 + array JSON | — |
| GET | /produtos/{id} | 200 + objeto JSON | 404 se não existir |
| POST | /produtos | 201 + objeto criado | 400 se body inválido |
| PUT | /produtos/{id} | 200 + objeto atualizado | 404 se não existir, 400 se inválido |
| DELETE | /produtos/{id} | 204 No Content | 404 se não existir |

## Arquivos principais

- `backend/cantinmoci/src/main/java/com/cantinmoci/controller/ProdutoController.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/service/ProdutoService.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/repository/ProdutoRepository.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/model/Produto.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/dto/ProdutoRequestDTO.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/dto/ProdutoResponseDTO.java`
- `backend/cantinmoci/src/main/java/com/cantinmoci/exception/ResourceNotFoundException.java`

## Roteiro de testes

- Arquivo: `docs/qa/test-produto.md`
- 10 testes documentados (caminho feliz + casos de erro)

## Critérios de aceitação — status de validação

| Critério | Status |
|---|---|
| Criar produto com dados válidos retorna 201 | Aguardando execução |
| Body inválido retorna 400 com mensagem | Aguardando execução |
| Buscar ID inexistente retorna 404 | Aguardando execução |
| Soft delete não apaga o registro | Aguardando execução |
| Listagem retorna apenas produtos ativos | Aguardando execução |
| PUT atualiza preço/estoque sem alterar `ativo` | Aguardando execução |

## Casos de erro mapeados

- `POST {}` — body vazio: esperado 400 com mensagens de validação para os 3 campos
- `GET /produtos/999` — ID inexistente: esperado 404 com mensagem "Produto nao encontrado com id: 999"
- `PUT /produtos/999` — atualizar produto inexistente: esperado 404
- `DELETE /produtos/999` — deletar produto inexistente: esperado 404

## Decisões de design

- Soft delete escolhido para preservar histórico de vendas futuras
- `buscarPorId` retorna produto independente do campo `ativo` (útil para auditoria)
- `listarAtivos` usa `findByAtivoTrue()` no repository (query derivada do Spring Data JPA)
- ProdutoRequestDTO não expõe `id` nem `ativo` — cliente não controla esses campos

## Relacionado

- [[fase-1-setup]]
- [[fase-3-auth]]
- [[arquitetura]]
