package com.cantinmoci.dto;

import com.cantinmoci.model.Cargo;

/**
 * DTO de saida para o usuario recem-cadastrado (POST /auth/register).
 *
 * Por que nao retornar a entidade Usuario diretamente?
 *   Usuario tem o campo "senha" (hash BCrypt) — mesmo sendo um hash, nao
 *   ha motivo para expor isso na resposta da API. Este DTO devolve so os
 *   dados seguros de exibir: id, nome, email e cargo.
 */
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private Cargo cargo;

    public UsuarioResponseDTO(Long id, String nome, String email, Cargo cargo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
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
}
