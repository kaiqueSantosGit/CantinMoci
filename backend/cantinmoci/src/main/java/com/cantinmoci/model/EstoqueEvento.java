package com.cantinmoci.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Entidade JPA que representa a tabela "estoque_evento" — a "ponte" entre
 * Evento e Produto.
 *
 * A partir da Fase 6, o estoque usado nas vendas NAO e mais o
 * Produto.quantidadeEmEstoque global (Fase 2/5) — e o estoque especifico
 * que foi alocado para CADA evento. Isso permite que o mesmo produto
 * (ex: "Coxinha") tenha 50 unidades no Evento A e 30 no Evento B, contados
 * de forma totalmente independente.
 *
 * quantidadeInicial — quanto foi alocado no total pro evento (soma de
 *   todas as alocacoes feitas via POST /eventos/{id}/produtos).
 * quantidadeAtual   — quanto ainda resta, depois dos descontos das vendas
 *   finalizadas. E este campo que o VendaService confere e desconta.
 *
 * @Version — mesmo mecanismo de lock otimista que tinha no Produto na
 * Fase 5, agora aqui: protege contra duas vendas do mesmo evento
 * finalizando ao mesmo tempo e descontando o mesmo produto de forma
 * inconsistente.
 */
@Entity
@Table(name = "estoque_evento")
public class EstoqueEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "quantidade_inicial", nullable = false)
    private Integer quantidadeInicial = 0;

    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual = 0;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidadeInicial() {
        return quantidadeInicial;
    }

    public void setQuantidadeInicial(Integer quantidadeInicial) {
        this.quantidadeInicial = quantidadeInicial;
    }

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Integer quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
