package com.cantinmoci.dto;

/**
 * DTO de saida para o endpoint POST /auth/login.
 *
 * Representa o corpo JSON que o servidor retorna apos um login bem-sucedido:
 *
 *   {
 *     "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1..."
 *   }
 *
 * O cliente deve guardar este token (geralmente no localStorage do browser
 * ou em memoria) e envia-lo em todas as requisicoes subsequentes no header:
 *   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 *
 * Por que so retornar o token?
 *   O token JWT ja contem informacoes embutidas (email do usuario, cargo,
 *   data de expiracao). O cliente pode decodificar o payload do JWT para
 *   ler essas informacoes sem precisar fazer outra requisicao ao servidor.
 */
public class TokenResponseDTO {

    // O token JWT gerado — string codificada em Base64 com tres partes separadas por ponto:
    // header.payload.signature
    private String token;

    // Construtor vazio — necessario para o Jackson serializar o objeto em JSON
    public TokenResponseDTO() {
    }

    // Construtor com o token — usado pelo AuthService para criar a resposta
    public TokenResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
