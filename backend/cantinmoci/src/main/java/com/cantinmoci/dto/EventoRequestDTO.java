package com.cantinmoci.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para POST /eventos.
 *   { "nome": "Festa Junina 2026", "local": "Salao paroquial" }
 * "local" e opcional (sem @NotBlank) — so informativo.
 */
public class EventoRequestDTO {

    @NotBlank(message = "O nome do evento e obrigatorio")
    private String nome;

    private String local;

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
}
