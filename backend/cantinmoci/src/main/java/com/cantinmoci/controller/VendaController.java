package com.cantinmoci.controller;

import com.cantinmoci.dto.AtualizarQuantidadeItemDTO;
import com.cantinmoci.dto.ItemVendaRequestDTO;
import com.cantinmoci.dto.VendaResponseDTO;
import com.cantinmoci.model.StatusVenda;
import com.cantinmoci.model.Usuario;
import com.cantinmoci.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST da entidade Venda (carrinho + venda finalizada).
 *
 * Todas as rotas exigem token valido (nao ha rota publica aqui) — qualquer
 * usuario autenticado, ADMIN ou OPERADOR, pode operar vendas. A restricao
 * por cargo (feita so em /auth/register) nao se aplica neste modulo.
 */
@RestController
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    // =========================================================================
    // POST /vendas
    // Abre um novo carrinho, vinculado ao operador logado.
    // =========================================================================

    /**
     * @AuthenticationPrincipal Usuario usuarioLogado — o Spring Security
     * injeta aqui o objeto autenticado da requisicao atual (o mesmo que o
     * JwtAuthFilter registrou no SecurityContextHolder apos validar o
     * token). Como a entidade Usuario ja implementa UserDetails, o Spring
     * consegue injetar o Usuario diretamente, sem conversao manual.
     */
    @PostMapping
    public ResponseEntity<VendaResponseDTO> abrir(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.abrirVenda(usuarioLogado));
    }

    // =========================================================================
    // POST /vendas/{id}/itens
    // Adiciona um produto ao carrinho.
    // =========================================================================

    @PostMapping("/{id}/itens")
    public ResponseEntity<VendaResponseDTO> adicionarItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemVendaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.adicionarItem(id, dto));
    }

    // =========================================================================
    // PUT /vendas/{id}/itens/{itemId}
    // Ajusta a quantidade de um item ja no carrinho.
    // =========================================================================

    @PutMapping("/{id}/itens/{itemId}")
    public ResponseEntity<VendaResponseDTO> atualizarQuantidade(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody AtualizarQuantidadeItemDTO dto) {
        return ResponseEntity.ok(vendaService.atualizarQuantidade(id, itemId, dto));
    }

    // =========================================================================
    // DELETE /vendas/{id}/itens/{itemId}
    // Remove um item do carrinho.
    // =========================================================================

    @DeleteMapping("/{id}/itens/{itemId}")
    public ResponseEntity<VendaResponseDTO> removerItem(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(vendaService.removerItem(id, itemId));
    }

    // =========================================================================
    // GET /vendas/{id}
    // Consulta uma venda especifica (carrinho aberto ou venda finalizada).
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    // =========================================================================
    // GET /vendas
    // Lista vendas — todas, ou filtradas por status (?status=FINALIZADA).
    // =========================================================================

    /**
     * @RequestParam(required = false) — o parametro "status" e opcional na URL.
     * Exemplos:
     *   GET /vendas                    → lista tudo (abertas e finalizadas)
     *   GET /vendas?status=FINALIZADA  → so o historico de vendas fechadas
     *   GET /vendas?status=ABERTA      → so carrinhos em andamento
     */
    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> listar(
            @RequestParam(required = false) StatusVenda status) {
        return ResponseEntity.ok(vendaService.listar(status));
    }

    // =========================================================================
    // POST /vendas/{id}/finalizar
    // Fecha o carrinho: valida e desconta o estoque, vira venda de verdade.
    // =========================================================================

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<VendaResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.finalizar(id));
    }
}
