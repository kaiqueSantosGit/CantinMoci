package com.cantinmoci.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para o endpoint POST /vendas/{id}/itens.
 *
 * Representa o corpo JSON que o operador envia para adicionar um produto
 * ao carrinho:
 *
 *   {
 *     "produtoId": 3,
 *     "quantidade": 2
 *   }
 *
 * Note que NAO recebemos "precoUnitario" aqui — o preco usado na venda e
 * sempre o preco atual do Produto no banco (o Service busca e faz o
 * snapshot), nunca um valor informado pelo cliente. Isso evita que alguem
 * manipule a requisicao para vender por um preco diferente do cadastrado.
 */
public class ItemVendaRequestDTO {

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
