package com.cantinmoci.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidade JPA que representa a tabela "eventos" no banco PostgreSQL.
 *
 * Um Evento agrupa vendas e o estoque alocado para elas — e o que permite
 * responder "quanto vendemos NESTE evento especifico", separado de outros
 * eventos que a instituicao ja fez ou vai fazer.
 *
 * So pode existir um Evento com status ABERTO por vez no sistema inteiro
 * (o EventoService valida isso ao criar um novo). Isso e o que permite a
 * Venda descobrir sozinha a qual evento ela pertence, sem o operador
 * precisar informar nada.
 */
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Local do evento — opcional, so informativo.
    private String local;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status = StatusEvento.ABERTO;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    // Nulo enquanto o evento esta ABERTO — preenchido so ao encerrar.
    @Column(name = "data_encerramento")
    private LocalDateTime dataEncerramento;

    // =========================================================================
    // GETTERS E SETTERS
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

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public void setStatus(StatusEvento status) {
        this.status = status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDateTime dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }
}
