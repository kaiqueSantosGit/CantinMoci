package com.cantinmoci.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para o endpoint PUT /vendas/{id}/itens/{itemId}.
 *
 * So permite alterar a quantidade de um item ja existente no carrinho —
 * para trocar o produto, o operador remove o item (DELETE) e adiciona um
 * novo (POST), em vez de "editar" o produto de um item existente.
 *
 *   { "quantidade": 5 }
 */
public class AtualizarQuantidadeItemDTO {

    @NotNull(message = "A quantidade e obrigatoria")
    @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
    private Integer quantidade;

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
