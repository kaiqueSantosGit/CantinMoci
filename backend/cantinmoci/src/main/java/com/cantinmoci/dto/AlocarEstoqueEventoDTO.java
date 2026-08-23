package com.cantinmoci.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para POST /eventos/{id}/produtos.
 *   { "produtoId": 3, "quantidade": 50 }
 *
 * Chamar este endpoint varias vezes para o mesmo produto SOMA a quantidade
 * (permite reforcar o estoque durante o evento, ex: "chegou mais Coxinha"),
 * em vez de substituir o valor anterior.
 */
public class AlocarEstoqueEventoDTO {

    @NotNull(message = "O produto e obrigatorio")
    private Long produtoId;

    @NotNull(message = "A quantidade e obrigatoria")
    @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
    private Integer quantidade;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
