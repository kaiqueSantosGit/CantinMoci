package com.cantinmoci.config;

import com.cantinmoci.service.JwtService;
import com.cantinmoci.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro HTTP que intercepta TODAS as requisicoes e valida o token JWT.
 *
 * O que e um filtro no Spring?
 *   Um filtro e um componente que intercepta a requisicao ANTES de ela
 *   chegar ao controller. Podemos ler headers, validar dados e decidir
 *   se a requisicao prossegue ou e bloqueada.
 *
 * Por que extends OncePerRequestFilter?
 *   Garante que este filtro execute exatamente UMA vez por requisicao,
 *   mesmo em cenarios com redirecionamentos internos do Spring.
 *
 * Fluxo deste filtro para cada requisicao:
 *   1. Le o header "Authorization" da requisicao
 *   2. Se nao tiver token (ou nao comecar com "Bearer "), deixa passar sem autenticar
 *      (o SecurityConfig vai barrar se a rota for protegida)
 *   3. Se tiver token, extrai o email do token
 *   4. Carrega o usuario do banco pelo email
 *   5. Valida o token — verifica assinatura e expiracao
 *   6. Se valido, registra o usuario como autenticado no SecurityContextHolder
 *   7. Deixa a requisicao continuar para o controller
 *
 * @Component — registra este filtro como componente do Spring.
 *   O SecurityConfig o adiciona explicitamente na cadeia de filtros.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    // JwtService — para extrair o email e validar o token
    private final JwtService jwtService;

    // UserDetailsServiceImpl — para carregar o usuario do banco pelo email
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Logica principal do filtro — executada a cada requisicao HTTP.
     *
     * @param request     — a requisicao HTTP recebida
     * @param response    — a resposta HTTP que sera enviada
     * @param filterChain — a cadeia de filtros — chame filterChain.doFilter()
     *                      para deixar a requisicao continuar para o proximo filtro
     *                      e eventualmente para o controller
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Passo 1: Le o valor do header "Authorization" da requisicao.
        // Esperamos receber: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        String authHeader = request.getHeader("Authorization");

        // Passo 2: Se o header nao existe ou nao comeca com "Bearer ",
        // esta requisicao nao tem token JWT. Deixamos passar para os
        // filtros seguintes (o SecurityConfig decidira se bloqueia ou nao).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // Para a execucao deste metodo aqui
        }

        // Passo 3: Extrai apenas a parte do token, removendo o prefixo "Bearer ".
        // "Bearer eyJhbGciOiJIUzI1NiJ9..." → "eyJhbGciOiJIUzI1NiJ9..."
        // substring(7) pula os 7 caracteres de "Bearer "
        String token = authHeader.substring(7);

        // Passo 4: Extrai o email embutido no payload do token.
        // Se o token for invalido (assinatura errada), o JwtService
        // lancara uma excecao aqui — a requisicao sera bloqueada.
        String email = jwtService.extrairEmail(token);

        // Passo 5: Verifica se o email foi extraido com sucesso E se o usuario
        // ainda nao esta autenticado nesta requisicao (evita processar duas vezes).
        // SecurityContextHolder.getContext().getAuthentication() — retorna null
        // se o usuario ainda nao foi autenticado nesta requisicao.
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Passo 6: Carrega o usuario completo do banco pelo email.
            // Precisamos do objeto Usuario para validar o token e para
            // registrar a autenticacao no SecurityContext.
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Passo 7: Valida o token — verifica se o email bate com o usuario
            // carregado do banco e se o token nao expirou.
            if (jwtService.isTokenValido(token, userDetails)) {

                // Passo 8: Cria o objeto de autenticacao do Spring Security.
                // UsernamePasswordAuthenticationToken representa um usuario autenticado.
                // Parametros:
                //   - userDetails         : quem e o usuario (email, permissoes)
                //   - null                : credencial (senha) — nao precisamos mais apos validar o token
                //   - getAuthorities()    : as permissoes do usuario (ROLE_ADMIN ou ROLE_OPERADOR)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                // Adiciona detalhes da requisicao ao objeto de autenticacao
                // (IP do cliente, session ID, etc.) — boa pratica do Spring Security
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Passo 9: Registra o usuario como autenticado no SecurityContextHolder.
                // O SecurityContextHolder e um repositorio que guarda quem esta autenticado
                // no thread atual. Ao setar aqui, o Spring Security sabe que esta
                // requisicao foi autenticada com sucesso.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Passo 10: Deixa a requisicao continuar para o proximo filtro ou controller.
        // Se chegamos ate aqui, o usuario esta autenticado (ou a rota e publica).
        filterChain.doFilter(request, response);
    }
}
