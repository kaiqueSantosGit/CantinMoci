package com.cantinmoci.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de saida de GET /eventos/{id}/relatorio.
 *
 * Considera apenas vendas com status FINALIZADA — carrinhos ainda ABERTOS
 * nao entram na contabilidade (o dinheiro so "existe de verdade" quando a
 * venda e finalizada).
 */
public class RelatorioEventoDTO {

    private Long eventoId;
    private String nomeEvento;
    private BigDecimal valorTotalArrecadado;
    private Integer quantidadeVendas;
    private BigDecimal ticketMedio;
    private List<ProdutoVendidoDTO> produtosMaisVendidos;

    public RelatorioEventoDTO(Long eventoId, String nomeEvento, BigDecimal valorTotalArrecadado,
                               Integer quantidadeVendas, BigDecimal ticketMedio,
                               List<ProdutoVendidoDTO> produtosMaisVendidos) {
        this.eventoId = eventoId;
        this.nomeEvento = nomeEvento;
        this.valorTotalArrecadado = valorTotalArrecadado;
        this.quantidadeVendas = quantidadeVendas;
        this.ticketMedio = ticketMedio;
        this.produtosMaisVendidos = produtosMaisVendidos;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public BigDecimal getValorTotalArrecadado() {
        return valorTotalArrecadado;
    }

    public Integer getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public List<ProdutoVendidoDTO> getProdutosMaisVendidos() {
        return produtosMaisVendidos;
    }
}
