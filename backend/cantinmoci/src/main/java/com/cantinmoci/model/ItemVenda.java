package com.cantinmoci.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Entidade JPA que representa a tabela "itens_venda" no banco PostgreSQL.
 *
 * Cada linha representa "um produto dentro de uma venda" — ex: "3 unidades
 * de Coxinha, a R$ 5,00 cada, na venda #12".
 *
 * Ponto importante: guardamos "precoUnitario" aqui, uma COPIA do preco do
 * Produto no momento em que o item foi adicionado ao carrinho — nao uma
 * referencia que busca o preco atual toda vez. Isso garante que, se o preco
 * do produto mudar depois (em uma fase futura de edicao de produtos), as
 * vendas ja registradas continuam mostrando o valor correto de quando
 * aconteceram. Esse padrao se chama "snapshot" de dados historicos.
 */
@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A venda (carrinho) a qual este item pertence.
    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    // O produto vendido. Continuamos referenciando o Produto (para saber
    // nome, e para o Service conseguir checar/descontar o estoque dele),
    // mas o preco usado na venda vem do campo precoUnitario abaixo, nao
    // do preco atual do produto.
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    // Snapshot do preco do produto no momento em que foi adicionado ao carrinho.
    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
