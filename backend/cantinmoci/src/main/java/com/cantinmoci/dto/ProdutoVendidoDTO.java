package com.cantinmoci.dto;

import java.math.BigDecimal;

/**
 * Uma linha do ranking de "produtos mais vendidos" dentro do relatorio de
 * um evento (RelatorioEventoDTO.produtosMaisVendidos).
 */
public class ProdutoVendidoDTO {

    private Long produtoId;
    private String nomeProduto;
    private Integer quantidadeVendida;
    private BigDecimal valorArrecadado;

    public ProdutoVendidoDTO(Long produtoId, String nomeProduto,
                              Integer quantidadeVendida, BigDecimal valorArrecadado) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidadeVendida = quantidadeVendida;
        this.valorArrecadado = valorArrecadado;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public BigDecimal getValorArrecadado() {
        return valorArrecadado;
    }
}
