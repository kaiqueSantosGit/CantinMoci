package com.cantinmoci.service;

import com.cantinmoci.dto.LoginRequestDTO;
import com.cantinmoci.dto.RegisterRequestDTO;
import com.cantinmoci.dto.ResetarSenhaDTO;
import com.cantinmoci.dto.TokenResponseDTO;
import com.cantinmoci.dto.TrocarSenhaDTO;
import com.cantinmoci.dto.UsuarioResponseDTO;
import com.cantinmoci.exception.EmailJaCadastradoException;
import com.cantinmoci.exception.OperacaoInvalidaException;
import com.cantinmoci.exception.ResourceNotFoundException;
import com.cantinmoci.exception.UnauthorizedException;
import com.cantinmoci.model.Usuario;
import com.cantinmoci.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsavel pela logica de autenticacao.
 *
 * Responsabilidade unica deste service:
 *   Receber as credenciais (email + senha), verificar se sao validas
 *   e, se sim, gerar e retornar um token JWT.
 *
 * Fluxo de autenticacao:
 *   1. Recebe o LoginRequestDTO com email e senha em texto puro
 *   2. Busca o usuario no banco pelo email
 *   3. Compara a senha digitada com o hash BCrypt armazenado
 *   4. Se valido, gera o token JWT via JwtService
 *   5. Retorna o token encapsulado no TokenResponseDTO
 *
 * @Service — componente gerenciado pelo Spring (singleton).
 */
@Service
public class AuthService {

    // Repository para buscar o usuario no banco pelo email
    private final UsuarioRepository usuarioRepository;

    // PasswordEncoder — componente do Spring Security para:
    //   - encode(senha)          : criar o hash BCrypt de uma senha
    //   - matches(raw, encoded)  : comparar senha digitada com hash armazenado
    // A implementacao BCrypt e registrada como bean no SecurityConfig.
    private final PasswordEncoder passwordEncoder;

    // JwtService — responsavel por gerar o token JWT
    private final JwtService jwtService;

    // Construtor: o Spring injeta todas as dependencias automaticamente
    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================================================================
    // AUTENTICAR
    // Verifica credenciais e retorna o token JWT se validas.
    // =========================================================================

    /**
     * Autentica o usuario e retorna um token JWT.
     *
     * Por que nao usamos AuthenticationManager do Spring aqui?
     *   Poderíamos delegar ao AuthenticationManager, mas fazer manualmente
     *   deixa o fluxo mais explicito e facil de entender para quem esta aprendendo.
     *
     * @param dto — objeto com email e senha recebidos do cliente
     * @return    — TokenResponseDTO contendo o token JWT gerado
     */
    public TokenResponseDTO autenticar(LoginRequestDTO dto) {
        // Passo 1: Busca o usuario no banco pelo email.
        // Se o email nao existir, lanca excecao com mensagem generica.
        // IMPORTANTE: usamos a mesma mensagem para "email nao encontrado" e
        // "senha incorreta" intencionalmente — isso e uma pratica de seguranca.
        // Se dissessemos "email nao encontrado", um atacante saberia quais
        // emails existem no sistema. Com a mensagem generica, ele nao sabe.
        // UnauthorizedException (@ResponseStatus 401) faz o Spring responder
        // HTTP 401 automaticamente, em vez do 500 generico de antes.
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciais invalidas"));

        // Passo 1.5 (Fase 7): usuario desativado nao consegue logar.
        // Mesma mensagem generica de sempre — nao revelamos que a conta
        // existe mas esta desativada, mesma logica de nao revelar "email
        // nao encontrado" vs "senha errada".
        if (!usuario.getAtivo()) {
            throw new UnauthorizedException("Credenciais invalidas");
        }

        // Passo 2: Compara a senha digitada (texto puro) com o hash BCrypt.
        // passwordEncoder.matches("senha123", "$2a$10$xyz...") → true ou false
        // O BCrypt nunca decifra o hash — ele gera um novo hash da senha digitada
        // e compara com o hash armazenado.
        boolean senhaCorreta = passwordEncoder.matches(dto.getSenha(), usuario.getSenha());

        if (!senhaCorreta) {
            // Mesma mensagem generica — nao revelamos se o erro foi email ou senha
            throw new UnauthorizedException("Credenciais invalidas");
        }

        // Passo 3: Credenciais validas — gera o token JWT para este usuario.
        // Passamos o usuario diretamente porque ele implementa UserDetails.
        String token = jwtService.gerarToken(usuario);

        // Passo 4: Retorna o token encapsulado no DTO de resposta
        return new TokenResponseDTO(token);
    }

    // =========================================================================
    // CADASTRAR
    // Cria um novo usuario no sistema. So pode ser chamado por quem ja tem
    // um token valido de ADMIN — a restricao de acesso fica no SecurityConfig
    // (rota /auth/register exige ROLE_ADMIN), nao aqui no service.
    // =========================================================================

    /**
     * Cadastra um novo usuario.
     *
     * @param dto — nome, email, senha (texto puro) e cargo do novo usuario
     * @return    — dados seguros do usuario criado (sem a senha)
     */
    public UsuarioResponseDTO cadastrar(RegisterRequestDTO dto) {
        // Impede dois usuarios com o mesmo email — email e o "login" do
        // sistema, precisa ser unico (a coluna no banco tambem tem
        // unique = true, mas validar aqui devolve um erro mais claro
        // ao cliente do que deixar o banco rejeitar com uma excecao de SQL).
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException(
                    "Ja existe um usuario cadastrado com o email: " + dto.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        // Nunca salvamos a senha em texto puro — encode() gera o hash BCrypt.
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setCargo(dto.getCargo());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return toResponseDTO(usuarioSalvo);
    }

    // =========================================================================
    // PERFIL DO USUARIO LOGADO (Fase 8 — Frontend)
    // O login (POST /auth/login) devolve so o token — o frontend precisa
    // desta rota pra saber quem esta logado (nome, cargo) e montar a tela.
    // =========================================================================

    public UsuarioResponseDTO obterPerfil(Usuario usuarioLogado) {
        return toResponseDTO(usuarioLogado);
    }

    // =========================================================================
    // TROCAR A PROPRIA SENHA
    // POST-agnostico de cargo — qualquer usuario autenticado pode trocar a
    // propria senha, sem confirmar a senha atual (decisao do projeto).
    // =========================================================================

    /**
     * Troca a senha do usuario atualmente logado.
     *
     * @param usuarioLogado — injetado pelo Controller via @AuthenticationPrincipal
     * @param dto           — a nova senha (em texto puro, sera hasheada aqui)
     */
    public void trocarSenha(Usuario usuarioLogado, TrocarSenhaDTO dto) {
        usuarioLogado.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuarioLogado);
    }

    // =========================================================================
    // RESETAR SENHA DE OUTRO USUARIO (ADMIN)
    // Restricao de acesso (hasRole ADMIN) fica no SecurityConfig, nao aqui.
    // =========================================================================

    public void resetarSenha(Long usuarioId, ResetarSenhaDTO dto) {
        Usuario usuario = buscarUsuarioOuFalhar(usuarioId);
        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    // =========================================================================
    // LISTAR USUARIOS ATIVOS (ADMIN)
    // =========================================================================

    public List<UsuarioResponseDTO> listarAtivos() {
        return usuarioRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // DESATIVAR USUARIO (ADMIN) — soft delete, mesmo padrao do Produto
    // =========================================================================

    /**
     * Desativa um usuario. A partir daqui, ele nao consegue mais logar nem
     * usar nenhum token ja emitido (ver Usuario.isEnabled() e JwtAuthFilter).
     *
     * @param usuarioId     — quem vai ser desativado
     * @param usuarioLogado — quem esta pedindo a desativacao (o ADMIN logado)
     */
    public void desativar(Long usuarioId, Usuario usuarioLogado) {
        // Impede um ADMIN de desativar a propria conta — evita ficar
        // travado de fora do sistema por engano (ainda mais critico se for
        // o unico ADMIN cadastrado).
        if (usuarioLogado.getId().equals(usuarioId)) {
            throw new OperacaoInvalidaException("Voce nao pode desativar a propria conta");
        }

        Usuario usuario = buscarUsuarioOuFalhar(usuarioId);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    // =========================================================================
    // METODOS AUXILIARES PRIVADOS
    // =========================================================================

    private Usuario buscarUsuarioOuFalhar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id: " + id));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCargo(),
                usuario.getAtivo());
    }
}
