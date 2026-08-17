package com.cantinmoci.dto;

import com.cantinmoci.model.Cargo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para o endpoint POST /auth/register.
 *
 * Representa o corpo JSON que o cliente (um ADMIN ja autenticado) envia
 * para cadastrar um novo usuario:
 *
 *   {
 *     "nome": "Maria Silva",
 *     "email": "maria@cantinmoci.com",
 *     "senha": "senhaSegura123",
 *     "cargo": "OPERADOR"
 *   }
 *
 * Mesmo padrao do ProdutoRequestDTO: validacoes via Bean Validation,
 * aplicadas automaticamente pelo Spring ao converter o JSON recebido.
 */
public class RegisterRequestDTO {

    @NotBlank(message = "O nome e obrigatorio")
    private String nome;

    @NotBlank(message = "O email e obrigatorio")
    private String email;

    // Senha em texto puro recebida do cliente — o AuthService a converte
    // em hash BCrypt antes de salvar. Nunca e armazenada nem logada assim.
    @NotBlank(message = "A senha e obrigatoria")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    // Cargo do novo usuario (ADMIN ou OPERADOR). O Jackson converte a
    // String do JSON diretamente para o enum Cargo.
    @NotNull(message = "O cargo e obrigatorio")
    private Cargo cargo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
}
