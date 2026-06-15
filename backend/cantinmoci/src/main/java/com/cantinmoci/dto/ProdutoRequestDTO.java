package com.cantinmoci.dto;

// Importacoes das anotacoes de validacao.
// Essas anotacoes serao aplicadas quando o Spring receber o JSON
// do cliente e tentar convertê-lo neste objeto (processo chamado "binding").
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) de ENTRADA para Produto.
 *
 * Este objeto representa o corpo (body) do JSON que o cliente envia
 * nas requisicoes POST (criar) e PUT (atualizar).
 *
 * Por que usar um DTO separado da entidade?
 *   - A entidade Produto tem campos que o cliente nao deve enviar,
 *     como "id" (gerado pelo banco) e "ativo" (gerenciado pelo sistema).
 *   - Com o DTO, controlamos exatamente o que o cliente pode informar.
 *   - Separar responsabilidades: entidade cuida do banco, DTO cuida da API.
 */
public class ProdutoRequestDTO {

    // Nome do produto — nao pode ser nulo, vazio ou apenas espacos em branco.
    @NotBlank(message = "O nome do produto e obrigatorio")
    private String nome;

    // Preco do produto — nao pode ser nulo nem negativo.
    // BigDecimal e o tipo correto para valores monetarios (evita erros de arredondamento).
    @NotNull(message = "O preco e obrigatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "O preco nao pode ser negativo")
    private BigDecimal preco;

    // Quantidade em estoque — nao pode ser nulo nem negativo.
    @NotNull(message = "A quantidade em estoque e obrigatoria")
    @Min(value = 0, message = "A quantidade em estoque nao pode ser negativa")
    private Integer quantidadeEmEstoque;

    // =========================================================================
    // GETTERS E SETTERS
    // O Spring usa esses metodos para preencher os campos ao ler o JSON recebido.
    // =========================================================================

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
}
