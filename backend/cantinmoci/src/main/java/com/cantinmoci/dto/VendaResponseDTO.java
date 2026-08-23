package com.cantinmoci.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de saida para Venda — representa tanto um carrinho ainda ABERTO
 * quanto uma venda ja FINALIZADA (o campo "status" diz qual dos dois e).
 *
 * Devolvido por praticamente todos os endpoints do modulo de Vendas
 * (abrir, adicionar item, atualizar item, remover item, finalizar,
 * consultar) — assim o cliente sempre recebe o estado completo e atual
 * do carrinho/venda apos qualquer acao, sem precisar fazer uma segunda
 * requisicao para saber o total atualizado.
 */
public class VendaResponseDTO {

    private Long id;
    private String status;
    private Long usuarioId;
    private String nomeUsuario;
    // Nulos para vendas antigas (criadas antes da Fase 6, sem evento vinculado).
    private Long eventoId;
    private String nomeEvento;
    private List<ItemVendaResponseDTO> itens;
    private BigDecimal valorTotal;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFinalizacao;

    public VendaResponseDTO(Long id, String status, Long usuarioId, String nomeUsuario,
                             Long eventoId, String nomeEvento,
                             List<ItemVendaResponseDTO> itens, BigDecimal valorTotal,
                             LocalDateTime dataAbertura, LocalDateTime dataFinalizacao) {
        this.id = id;
        this.status = status;
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.eventoId = eventoId;
        this.nomeEvento = nomeEvento;
        this.itens = itens;
        this.valorTotal = valorTotal;
        this.dataAbertura = dataAbertura;
        this.dataFinalizacao = dataFinalizacao;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public List<ItemVendaResponseDTO> getItens() {
        return itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }
}
