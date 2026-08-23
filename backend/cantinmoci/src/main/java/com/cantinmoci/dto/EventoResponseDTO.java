package com.cantinmoci.dto;

import java.time.LocalDateTime;

public class EventoResponseDTO {

    private Long id;
    private String nome;
    private String local;
    private String status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataEncerramento;

    public EventoResponseDTO(Long id, String nome, String local, String status,
                              LocalDateTime dataAbertura, LocalDateTime dataEncerramento) {
        this.id = id;
        this.nome = nome;
        this.local = local;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.dataEncerramento = dataEncerramento;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLocal() {
        return local;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataEncerramento() {
        return dataEncerramento;
    }
}
