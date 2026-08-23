package com.cantinmoci.dto;

import com.cantinmoci.model.Cargo;

/**
 * DTO de saida para dados de usuario — usado por POST /auth/register e,
 * desde a Fase 7, tambem por GET /usuarios.
 *
 * Por que nao retornar a entidade Usuario diretamente?
 *   Usuario tem o campo "senha" (hash BCrypt) — mesmo sendo um hash, nao
 *   ha motivo para expor isso na resposta da API. Este DTO devolve so os
 *   dados seguros de exibir: id, nome, email, cargo e status.
 */
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private Cargo cargo;
    private Boolean ativo;

    public UsuarioResponseDTO(Long id, String nome, String email, Cargo cargo, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}
