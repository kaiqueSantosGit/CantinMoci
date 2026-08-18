package com.cantinmoci.dto;

import java.math.BigDecimal;

/**
 * DTO de saida — representa um item dentro da resposta de uma Venda.
 *
 * Inclui "nomeProduto" (nao so o ID) para o cliente nao precisar fazer uma
 * segunda requisicao so para mostrar o nome do produto na tela do carrinho.
 * Inclui "subtotal" ja calculado (quantidade * precoUnitario) pelo mesmo
 * motivo: poupa o cliente de fazer essa conta.
 */
public class ItemVendaResponseDTO {

    private Long id;
    private Long produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public ItemVendaResponseDTO(Long id, Long produtoId, String nomeProduto,
                                 Integer quantidade, BigDecimal precoUnitario,
                                 BigDecimal subtotal) {
        this.id = id;
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
