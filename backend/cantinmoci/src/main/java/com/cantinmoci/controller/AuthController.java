package com.cantinmoci.controller;

import com.cantinmoci.dto.LoginRequestDTO;
import com.cantinmoci.dto.RegisterRequestDTO;
import com.cantinmoci.dto.TokenResponseDTO;
import com.cantinmoci.dto.TrocarSenhaDTO;
import com.cantinmoci.dto.UsuarioResponseDTO;
import com.cantinmoci.model.Usuario;
import com.cantinmoci.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
     *   4. Se invalidas (ou usuario desativado): AuthService lanca
     *      UnauthorizedException → HTTP 401 automatico
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

    // =========================================================================
    // POST /auth/register
    // Cadastra um novo usuario. Rota protegida — exige token de um usuario
    // com cargo ADMIN (restricao configurada no SecurityConfig, nao aqui).
    // =========================================================================

    /**
     * POST /auth/register
     *
     * So pode ser chamado com um Bearer token valido de um usuario ADMIN.
     *
     * Fluxo:
     *   1. Cliente envia: { "nome", "email", "senha", "cargo" }
     *   2. @Valid aciona as validacoes do RegisterRequestDTO (campos
     *      obrigatorios, tamanho minimo de senha) — se invalido, HTTP 400
     *      automatico, antes mesmo do metodo ser executado.
     *   3. AuthService verifica se o email ja existe e cria o usuario
     *   4. Retorna HTTP 201 Created com os dados do usuario (sem a senha)
     *
     * @RequestBody @Valid RegisterRequestDTO dto — corpo JSON validado
     * @return ResponseEntity<UsuarioResponseDTO> — HTTP 201 com o usuario criado
     */
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(@RequestBody @Valid RegisterRequestDTO dto) {
        UsuarioResponseDTO resposta = authService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // =========================================================================
    // PUT /auth/me/senha
    // O usuario logado troca a propria senha. Rota protegida por token, sem
    // restricao de cargo — qualquer ADMIN ou OPERADOR pode trocar a propria.
    // =========================================================================

    /**
     * PUT /auth/me/senha
     *
     * @AuthenticationPrincipal Usuario usuarioLogado — o Spring Security
     * injeta o usuario autenticado da requisicao atual (mesmo mecanismo
     * usado em VendaController.abrir).
     *
     * Decisao do projeto: nao exige confirmar a senha atual, so a nova —
     * o token JWT valido ja e considerado prova suficiente de identidade.
     *
     * @return 204 No Content — trocou com sucesso, nao ha nada pra devolver.
     */
    @PutMapping("/me/senha")
    public ResponseEntity<Void> trocarSenha(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @Valid @RequestBody TrocarSenhaDTO dto) {
        authService.trocarSenha(usuarioLogado, dto);
        return ResponseEntity.noContent().build();
    }
}
