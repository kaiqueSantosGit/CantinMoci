package com.cantinmoci.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para PUT /auth/me/senha.
 *
 * O usuario logado troca a propria senha informando so a nova senha —
 * decisao do usuario do projeto: nao exigir confirmar a senha atual (o
 * token JWT valido ja e considerado prova suficiente de identidade aqui).
 *
 *   { "novaSenha": "minhaNovaSenha123" }
 */
public class TrocarSenhaDTO {

    @NotBlank(message = "A nova senha e obrigatoria")
    @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres")
    private String novaSenha;

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
