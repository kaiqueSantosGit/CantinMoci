package com.cantinmoci.model;

// Importacoes do Jakarta Persistence — sao as anotacoes que dizem ao JPA
// como mapear esta classe Java para uma tabela no banco de dados.
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Importacoes de validacao — anotacoes que garantem que os dados
// estao corretos antes de qualquer operacao.
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Entidade JPA que representa a tabela "produtos" no banco PostgreSQL.
 *
 * @Entity  — diz ao Spring que esta classe e uma entidade gerenciada pelo JPA.
 * @Table   — define o nome exato da tabela no banco. Sem isso, o JPA usaria
 *            o nome da classe em maiusculo.
 */
@Entity
@Table(name = "produtos")
public class Produto {

    /**
     * Chave primaria da tabela.
     * @Id               — marca este campo como a chave primaria (PK).
     * @GeneratedValue   — o banco gera o valor automaticamente (auto-increment).
     * GenerationType.IDENTITY — usa a sequencia IDENTITY do PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome do produto.
     * @NotBlank — nao aceita null, string vazia "" ou string so com espacos "   ".
     * @Column(nullable = false) — no banco, esta coluna nao pode ser nula.
     */
    @NotBlank(message = "O nome do produto e obrigatorio")
    @Column(nullable = false)
    private String nome;

    /**
     * Preco do produto.
     * BigDecimal e usado para valores monetarios porque evita erros de
     * arredondamento que ocorrem com double e float.
     *
     * @NotNull — nao aceita null (BigDecimal nao e String, entao usamos NotNull).
     * @DecimalMin — o valor minimo aceito. "0.00" com inclusive=true significa >= 0.
     * @Column(nullable = false, precision = 10, scale = 2) — no banco:
     *   precision = quantos digitos no total (ex: 99999999.99)
     *   scale     = quantas casas decimais (ex: ,99)
     */
    @NotNull(message = "O preco e obrigatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "O preco nao pode ser negativo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    /**
     * Quantidade disponivel em estoque.
     * @NotNull — nao aceita null.
     * @Min(0)  — o valor minimo e 0. Estoque nao pode ser negativo.
     * @Column(name = "quantidade_em_estoque") — nome explicito da coluna no banco,
     *   pois o JPA converteria "quantidadeEmEstoque" para "quantidade_em_estoque"
     *   automaticamente na maioria dos casos, mas deixar explicito e mais seguro.
     */
    @NotNull(message = "A quantidade em estoque e obrigatoria")
    @Min(value = 0, message = "A quantidade em estoque nao pode ser negativa")
    @Column(name = "quantidade_em_estoque", nullable = false)
    private Integer quantidadeEmEstoque;

    /**
     * Indica se o produto esta ativo no sistema.
     * Usado para "soft delete" — ao inves de apagar o registro do banco,
     * apenas marcamos como inativo. Isso preserva historico de vendas.
     *
     * O valor padrao e true: todo produto criado comeca como ativo.
     * @Column(nullable = false) — nao pode ser nulo no banco.
     */
    @Column(nullable = false)
    private Boolean ativo = true;

    // =========================================================================
    // GETTERS E SETTERS
    // Metodos publicos para ler (get) e escrever (set) cada campo.
    // O JPA e o Spring precisam desses metodos para montar e ler objetos.
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
