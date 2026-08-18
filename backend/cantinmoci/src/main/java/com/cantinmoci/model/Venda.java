package com.cantinmoci.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa a tabela "vendas" no banco PostgreSQL.
 *
 * Esta entidade cumpre DOIS papeis, dependendo do campo "status":
 *   - Enquanto ABERTA: funciona como o "carrinho" que o operador vai
 *     montando durante o atendimento (adicionar/ajustar/remover itens).
 *   - Quando FINALIZADA: vira o registro definitivo da venda, com o
 *     estoque ja descontado e o valor total fechado.
 *
 * Um unico ManyToOne aponta para o Usuario (operador) que abriu a venda —
 * pego automaticamente do token JWT, nunca informado pelo cliente.
 */
@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Status atual da venda — ver StatusVenda para o significado de cada valor.
     * @Enumerated(EnumType.STRING) — mesmo padrao usado em Usuario.cargo:
     * salva o nome do enum como texto ("ABERTA"/"FINALIZADA"), nao o indice
     * numerico, para o dado ficar legivel direto no banco.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVenda status = StatusVenda.ABERTA;

    /**
     * O operador que abriu esta venda.
     * @ManyToOne — varias vendas podem pertencer ao mesmo usuario.
     * @JoinColumn — nome da coluna de chave estrangeira no banco.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Soma de (precoUnitario * quantidade) de todos os itens.
     * Recalculado pelo Service toda vez que um item e adicionado, alterado
     * ou removido — assim o cliente sempre ve o total atualizado sem
     * precisar somar os itens manualmente.
     */
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    // Nulo enquanto a venda esta ABERTA — preenchido so no momento de finalizar.
    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    /**
     * Itens desta venda (o "conteudo do carrinho").
     *
     * @OneToMany(mappedBy = "venda") — o lado "um" da relacao. "mappedBy"
     *   diz que quem realmente controla a chave estrangeira e o campo
     *   "venda" la na entidade ItemVenda (evita criar uma tabela extra
     *   de associacao, que nao faria sentido aqui).
     * cascade = CascadeType.ALL — operacoes na Venda (salvar, deletar)
     *   se propagam automaticamente para os itens da lista. Ao adicionar
     *   um ItemVenda novo nesta lista e salvar a Venda, o item e salvo
     *   junto — nao precisamos chamar um repository de ItemVenda separado.
     * orphanRemoval = true — se um item for removido desta lista (list.remove()),
     *   o Hibernate apaga o registro dele do banco automaticamente ao salvar.
     */
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void setStatus(StatusVenda status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }
}
