package com.cantinmoci.config;

import com.cantinmoci.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracao central do Spring Security.
 *
 * Esta classe define TODAS as regras de seguranca da aplicacao:
 *   - Quais rotas sao publicas (nao precisam de token)
 *   - Quais rotas sao protegidas (exigem token valido)
 *   - Como os tokens JWT sao validados (qual filtro usar)
 *   - Como as senhas sao codificadas (BCrypt)
 *   - Que a API nao usa sessoes HTTP (stateless)
 *
 * @Configuration      — indica que esta classe contem definicoes de beans do Spring.
 * @EnableWebSecurity  — ativa o modulo de seguranca web do Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Nosso filtro JWT — intercepta requisicoes e valida o token
    private final JwtAuthFilter jwtAuthFilter;

    // Service que carrega o usuario do banco (usado pelo DaoAuthenticationProvider)
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    // =========================================================================
    // BEAN: SecurityFilterChain
    // Define as regras de autorizacao e a cadeia de filtros de seguranca.
    // =========================================================================

    /**
     * Define as regras de seguranca HTTP da aplicacao.
     *
     * Por que o DaoAuthenticationProvider esta aqui dentro e nao como @Bean separado?
     *   No Spring Security 6 / Spring Boot 3.5, quando voce registra um
     *   AuthenticationProvider como @Bean, o Spring emite um aviso dizendo que
     *   o UserDetailsService bean sera ignorado. Para evitar esse aviso e deixar
     *   a configuracao mais limpa, criamos o provider diretamente aqui dentro e
     *   o registramos via .authenticationProvider() — sem expor como bean global.
     *
     * @param http — objeto de configuracao do Spring Security, injetado automaticamente
     * @return     — a cadeia de filtros de seguranca configurada
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Cria o provider que sabe como autenticar usuarios.
        // DaoAuthenticationProvider — implementacao padrao do Spring Security que:
        //   1. Usa o UserDetailsService para carregar o usuario do banco pelo email
        //   2. Usa o PasswordEncoder (BCrypt) para comparar a senha digitada com o hash
        // Spring Boot 3.5+: o construtor preferido recebe o UserDetailsService diretamente.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        http
            // CSRF (Cross-Site Request Forgery) — ataque que usa cookies de sessao.
            // APIs REST stateless NAO usam cookies de sessao, entao CSRF nao se aplica.
            // Desabilitamos para simplificar o uso com Postman e clientes REST.
            .csrf(AbstractHttpConfigurer::disable)

            // Configuracao de autorizacao por rota
            .authorizeHttpRequests(auth -> auth
                // Rotas PUBLICAS — nao exigem autenticacao:
                //   GET /health       — verificar se a API esta no ar
                //   POST /auth/login  — endpoint de login (obter o token)
                //   /error            — rota interna do Spring, usada para
                //     renderizar a resposta de erro (ex: 401, 404). Quando um
                //     controller lanca uma excecao com @ResponseStatus, o
                //     Spring redireciona internamente para /error antes de
                //     montar o corpo JSON da resposta. Por padrao, o Spring
                //     Security tambem filtra esse redirecionamento interno —
                //     sem liberar /error aqui, ele barrava esse segundo
                //     acesso e sobrescrevia QUALQUER erro (401, 404, etc.)
                //     para 403, escondendo o status real da excecao original.
                .requestMatchers("/health", "/auth/login", "/error").permitAll()

                // POST /auth/register exige token valido E cargo ADMIN.
                // hasRole("ADMIN") verifica a authority "ROLE_ADMIN" (o
                // prefixo "ROLE_" e adicionado automaticamente pelo metodo —
                // e o mesmo padrao que Usuario.getAuthorities() gera).
                // Precisa vir ANTES do anyRequest().authenticated() abaixo:
                // o Spring Security avalia essas regras na ordem declarada,
                // e usa a primeira que combinar com a rota da requisicao.
                .requestMatchers(HttpMethod.POST, "/auth/register").hasRole("ADMIN")

                // Fase 7: toda a gestao de usuarios (listar, desativar,
                // resetar senha de outro usuario) exige ADMIN. Trocar a
                // PROPRIA senha (PUT /auth/me/senha) NAO entra aqui —
                // fica coberto pelo anyRequest().authenticated() abaixo,
                // qualquer usuario logado pode trocar a propria senha.
                .requestMatchers("/usuarios/**").hasRole("ADMIN")

                // Todas as demais rotas exigem um token JWT valido.
                // anyRequest() — qualquer rota nao listada acima
                // authenticated() — exige que o usuario esteja autenticado
                .anyRequest().authenticated()
            )

            // Sessao STATELESS — a API nao cria nem usa sessoes HTTP no servidor.
            // Cada requisicao e independente e deve carregar o token JWT.
            // Isso e essencial para APIs REST escaláveis (pode rodar em multiplos servidores).
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Registra o provider criado acima diretamente na cadeia de seguranca.
            // Ao registrar aqui (em vez de como @Bean), evitamos o aviso do Spring
            // sobre conflito entre AuthenticationProvider bean e UserDetailsService bean.
            .authenticationProvider(authProvider)

            // Adiciona nosso filtro JWT ANTES do filtro padrao de autenticacao.
            // Isso garante que o token JWT seja validado primeiro, antes de qualquer
            // outra logica de autenticacao do Spring Security.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // =========================================================================
    // BEAN: PasswordEncoder
    // Define como as senhas serao codificadas e verificadas.
    // =========================================================================

    /**
     * Define o algoritmo de codificacao de senhas: BCrypt.
     *
     * BCrypt e um algoritmo de hashing especialmente projetado para senhas:
     *   - Adiciona um "sal" (salt) aleatorio — duas senhas iguais geram hashes diferentes
     *   - E lento por design — dificulta ataques de forca bruta
     *   - O fator de custo (strength) define o quanto e lento (padrao: 10)
     *
     * Este bean e injetado no AuthService para comparar senhas no login.
     * Tambem pode ser usado futuramente para cadastrar novos usuarios.
     *
     * @Bean — registrado como bean para ser injetado no AuthService via construtor.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================================
    // BEAN: AuthenticationManager
    // Gerenciador central de autenticacao do Spring Security.
    // =========================================================================

    /**
     * Expoe o AuthenticationManager como bean.
     *
     * AuthenticationManager — interface central do Spring Security responsavel
     * por coordenar a autenticacao. Ele delega para o AuthenticationProvider.
     *
     * Por que expor como bean?
     *   Para que outros componentes possam injetar e usar o AuthenticationManager
     *   quando precisarem autenticar manualmente em flows mais complexos.
     *   Boa pratica para extensibilidade futura do projeto.
     *
     * @param config — configuracao de autenticacao injetada pelo Spring
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
