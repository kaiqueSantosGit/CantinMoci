package com.cantinmoci.controller;

import com.cantinmoci.dto.AlocarEstoqueEventoDTO;
import com.cantinmoci.dto.EstoqueEventoResponseDTO;
import com.cantinmoci.dto.EventoRequestDTO;
import com.cantinmoci.dto.EventoResponseDTO;
import com.cantinmoci.dto.RelatorioEventoDTO;
import com.cantinmoci.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST da entidade Evento.
 *
 * Todas as rotas exigem token valido (sem restricao por cargo — qualquer
 * usuario autenticado, ADMIN ou OPERADOR, pode gerenciar eventos, mesmo
 * padrao usado em /vendas).
 */
@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // POST /eventos — cria e abre um evento novo
    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(@Valid @RequestBody EventoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.criar(dto));
    }

    // POST /eventos/{id}/encerrar — encerra o evento
    @PostMapping("/{id}/encerrar")
    public ResponseEntity<EventoResponseDTO> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.encerrar(id));
    }

    // GET /eventos — lista todos os eventos
    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar() {
        return ResponseEntity.ok(eventoService.listar());
    }

    // GET /eventos/{id} — consulta um evento especifico
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    // POST /eventos/{id}/produtos — aloca (ou reforca) estoque de um produto pro evento
    @PostMapping("/{id}/produtos")
    public ResponseEntity<EstoqueEventoResponseDTO> alocarEstoque(
            @PathVariable Long id,
            @Valid @RequestBody AlocarEstoqueEventoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.alocarEstoque(id, dto));
    }

    // GET /eventos/{id}/produtos — lista os produtos + estoque disponivel neste evento
    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<EstoqueEventoResponseDTO>> listarProdutos(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.listarProdutosDoEvento(id));
    }

    // GET /eventos/{id}/relatorio — total arrecadado, qtd vendas, ticket medio, ranking de produtos
    @GetMapping("/{id}/relatorio")
    public ResponseEntity<RelatorioEventoDTO> relatorio(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.gerarRelatorio(id));
    }
}
