package com.cantinmoci.controller;

import com.cantinmoci.dto.LoginRequestDTO;
import com.cantinmoci.dto.TokenResponseDTO;
import com.cantinmoci.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST responsavel pelos endpoints de autenticacao.
 *
 * Seguindo o mesmo padrao do ProdutoController:
 *   - Recebe a requisicao HTTP
 *   - Delega ao Service (AuthService)
 *   - Retorna a resposta HTTP com o status correto
 *   - Nao contem nenhuma regra de negocio
 *
 * @RestController      — combinacao de @Controller + @ResponseBody.
 *   Todos os retornos sao automaticamente serializados para JSON.
 *
 * @RequestMapping("/auth") — prefixo de todas as rotas deste controller.
 *   Todos os endpoints aqui respondem em URLs que comecam com /auth.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // O service e injetado via construtor — mesmo padrao do ProdutoController
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================================================================
    // POST /auth/login
    // Recebe email e senha, valida as credenciais e retorna um token JWT.
    // =========================================================================

    /**
     * POST /auth/login
     *
     * Endpoint publico (configurado no SecurityConfig como permitAll).
     * Nao precisa de token para ser acessado — e o proprio endpoint que gera tokens.
     *
     * Fluxo:
     *   1. Cliente envia: { "email": "...", "senha": "..." }
     *   2. AuthService valida as credenciais
     *   3. Se validas: retorna HTTP 200 OK com { "token": "eyJ..." }
     *   4. Se invalidas: AuthService lanca RuntimeException → HTTP 500
     *      (nas proximas fases melhoraremos o tratamento de erro para HTTP 401)
     *
     * @RequestBody LoginRequestDTO dto — o Spring converte o JSON recebido
     *   automaticamente para um objeto LoginRequestDTO.
     *
     * @return ResponseEntity<TokenResponseDTO> — HTTP 200 com o token no corpo.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        // Delega toda a logica para o AuthService e retorna o resultado com HTTP 200
        TokenResponseDTO resposta = authService.autenticar(dto);
        return ResponseEntity.ok(resposta);
    }
}
