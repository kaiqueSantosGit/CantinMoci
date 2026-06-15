package com.cantinmoci.dto;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) de SAIDA para Produto.
 *
 * Este objeto representa o corpo (body) do JSON que o servidor retorna
 * ao cliente nas respostas de GET, POST e PUT.
 *
 * Por que usar um DTO de saida separado?
 *   - Controlamos exatamente quais campos aparecem na resposta.
 *   - No futuro, podemos adicionar campos calculados (ex: "precoFormatado")
 *     sem alterar a entidade.
 *   - Seguranca: evitamos expor campos internos da entidade acidentalmente.
 *
 * Neste DTO incluimos o "id" e o "ativo" pois o cliente precisa saber
 * o identificador do recurso criado e seu status atual.
 */
public class ProdutoResponseDTO {

    // Identificador unico gerado pelo banco. O cliente precisa desse valor
    // para fazer requisicoes futuras (ex: GET /produtos/1, PUT /produtos/1).
    private Long id;

    // Nome do produto.
    private String nome;

    // Preco do produto.
    private BigDecimal preco;

    // Quantidade atual em estoque.
    private Integer quantidadeEmEstoque;

    // Indica se o produto esta ativo. False significa que foi "deletado" (soft delete).
    private Boolean ativo;

    // =========================================================================
    // CONSTRUTORES
    // Fornecemos dois: um vazio (exigido pelo Jackson para desserializar JSON)
    // e um completo (usado pelo Service para montar a resposta de forma concisa).
    // =========================================================================

    // Construtor vazio — o Jackson (biblioteca que converte objetos Java em JSON)
    // precisa deste construtor para funcionar corretamente.
    public ProdutoResponseDTO() {}

    // Construtor completo — usado no Service para criar a resposta em uma linha.
    public ProdutoResponseDTO(Long id, String nome, BigDecimal preco,
                              Integer quantidadeEmEstoque, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEmEstoque = quantidadeEmEstoque;
        this.ativo = ativo;
    }

    // =========================================================================
    // GETTERS E SETTERS
    // O Jackson usa os getters para serializar o objeto em JSON na resposta HTTP.
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getQuantidadeEmEstoque() {
        return quantidadeEmEstoque;
    }

    public void setQuantidadeEmEstoque(Integer quantidadeEmEstoque) {
        this.quantidadeEmEstoque = quantidadeEmEstoque;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
