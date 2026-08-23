package com.cantinmoci.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para PUT /usuarios/{id}/senha.
 *
 * Um ADMIN reseta a senha de outro usuario informando a nova senha
 * diretamente — mesmo padrao ja usado em POST /auth/register.
 *
 *   { "novaSenha": "senhaTemporaria123" }
 */
public class ResetarSenhaDTO {

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
