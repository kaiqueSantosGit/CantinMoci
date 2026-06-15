package com.cantinmoci.controller;

import com.cantinmoci.dto.ProdutoRequestDTO;
import com.cantinmoci.dto.ProdutoResponseDTO;
import com.cantinmoci.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST da entidade Produto.
 *
 * O Controller e a camada de ENTRADA da API.
 * Ele recebe requisicoes HTTP, delega o trabalho ao Service
 * e devolve a resposta HTTP ao cliente.
 *
 * Responsabilidades do Controller:
 *   - Mapear rotas HTTP para metodos Java.
 *   - Receber e validar os dados da requisicao.
 *   - Chamar o Service com os dados recebidos.
 *   - Retornar o status HTTP correto com o corpo da resposta.
 *   - O Controller NAO deve conter regras de negocio — isso e trabalho do Service.
 *
 * @RestController — combinacao de @Controller + @ResponseBody.
 *   Diz ao Spring que esta classe trata requisicoes HTTP e que os retornos
 *   dos metodos serao automaticamente serializados para JSON.
 *
 * @RequestMapping("/produtos") — prefixo base para todos os endpoints desta classe.
 *   Todos os metodos aqui respondem em URLs que comecam com /produtos.
 */
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    // O service e injetado via construtor (injecao de dependencia).
    // O Controller conhece apenas o Service — nunca o Repository diretamente.
    private final ProdutoService produtoService;

    // Construtor: o Spring injeta automaticamente o ProdutoService.
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // =========================================================================
    // GET /produtos
    // Lista todos os produtos ativos.
    // =========================================================================

    /**
     * GET /produtos
     *
     * Retorna a lista de todos os produtos com ativo = true.
     * HTTP 200 OK com o array JSON no corpo da resposta.
     *
     * ResponseEntity<List<ProdutoResponseDTO>> — encapsula:
     *   - O status HTTP (200, 201, 404, etc.)
     *   - Os headers da resposta
     *   - O corpo da resposta (o objeto que sera serializado em JSON)
     */
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarAtivos());
    }

    // =========================================================================
    // GET /produtos/{id}
    // Busca um produto especifico pelo ID.
    // =========================================================================

    /**
     * GET /produtos/{id}
     *
     * Retorna um unico produto pelo seu ID.
     * HTTP 200 OK se encontrado.
     * HTTP 404 Not Found se o ID nao existir (lancado pelo Service).
     *
     * @PathVariable Long id — captura o valor {id} da URL.
     *   Exemplo: GET /produtos/3 → id = 3
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    // =========================================================================
    // POST /produtos
    // Cria um novo produto.
    // =========================================================================

    /**
     * POST /produtos
     *
     * Recebe os dados do novo produto no corpo da requisicao (JSON),
     * cria no banco e retorna o produto criado com seu ID gerado.
     * HTTP 201 Created (nao 200 — a convencao REST usa 201 para criacao).
     *
     * @Valid — ativa a validacao das anotacoes (@NotBlank, @NotNull, etc.)
     *   definidas no ProdutoRequestDTO. Se alguma falhar, o Spring retorna
     *   HTTP 400 Bad Request automaticamente com os erros de validacao.
     *
     * @RequestBody — diz ao Spring para ler o corpo da requisicao HTTP
     *   e converter o JSON recebido para um objeto ProdutoRequestDTO.
     */
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(dto));
    }

    // =========================================================================
    // PUT /produtos/{id}
    // Atualiza todos os dados de um produto existente.
    // =========================================================================

    /**
     * PUT /produtos/{id}
     *
     * Recebe os novos dados no corpo da requisicao e atualiza o produto existente.
     * HTTP 200 OK com o produto atualizado.
     * HTTP 404 Not Found se o ID nao existir.
     *
     * PUT e usado para substituicao completa do recurso.
     * Todos os campos editaveis (nome, preco, quantidade) devem ser enviados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.ok(produtoService.atualizar(id, dto));
    }

    // =========================================================================
    // DELETE /produtos/{id}
    // Desativa um produto (soft delete — nao apaga do banco).
    // =========================================================================

    /**
     * DELETE /produtos/{id}
     *
     * Marca o produto como inativo (ativo = false).
     * HTTP 204 No Content — convencao REST para operacoes de delete bem-sucedidas.
     *   O "No Content" significa: "deu certo, mas nao ha nada para retornar".
     * HTTP 404 Not Found se o ID nao existir.
     *
     * ResponseEntity<Void> — Void indica que o corpo da resposta esta vazio,
     * o que e correto para o status 204.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        produtoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
