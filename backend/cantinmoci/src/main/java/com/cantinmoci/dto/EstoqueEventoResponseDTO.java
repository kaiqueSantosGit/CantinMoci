package com.cantinmoci.dto;

public class EstoqueEventoResponseDTO {

    private Long produtoId;
    private String nomeProduto;
    private Integer quantidadeInicial;
    private Integer quantidadeAtual;

    public EstoqueEventoResponseDTO(Long produtoId, String nomeProduto,
                                     Integer quantidadeInicial, Integer quantidadeAtual) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidadeInicial = quantidadeInicial;
        this.quantidadeAtual = quantidadeAtual;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getQuantidadeInicial() {
        return quantidadeInicial;
    }

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }
}
