package com.cantinmoci.service;

import com.cantinmoci.dto.LoginRequestDTO;
import com.cantinmoci.dto.RegisterRequestDTO;
import com.cantinmoci.dto.TokenResponseDTO;
import com.cantinmoci.dto.UsuarioResponseDTO;
import com.cantinmoci.exception.EmailJaCadastradoException;
import com.cantinmoci.exception.UnauthorizedException;
import com.cantinmoci.model.Usuario;
import com.cantinmoci.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getCargo());
    }
}
