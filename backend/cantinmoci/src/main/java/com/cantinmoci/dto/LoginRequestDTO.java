package com.cantinmoci.dto;

/**
 * DTO de entrada para o endpoint POST /auth/login.
 *
 * DTO (Data Transfer Object) — objeto que carrega dados entre camadas.
 * Este DTO representa o corpo JSON que o cliente envia para fazer login:
 *
 *   {
 *     "email": "admin@cantinmoci.com",
 *     "senha": "minhasenha123"
 *   }
 *
 * Por que usar um DTO em vez de receber os campos diretamente?
 *   - Agrupa os campos relacionados em um unico objeto.
 *   - Separa o contrato da API (o que o cliente envia) da entidade do banco.
 *   - Facilita adicionar validacoes sem alterar a entidade.
 *
 * Nota: nao usamos @NotBlank aqui porque o Spring Security ja cuida
 * da validacao das credenciais no AuthService.
 */
public class LoginRequestDTO {

    // Email usado como identificador de login
    private String email;

    // Senha em texto puro — sera comparada com o hash BCrypt no AuthService
    // NUNCA armazenamos ou logamos este valor
    private String senha;

    // Construtor vazio — necessario para o Jackson desserializar o JSON recebido
    public LoginRequestDTO() {
    }

    // Construtor completo — util para testes e para criar instancias manualmente
    public LoginRequestDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
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
}
