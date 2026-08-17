package com.cantinmoci.service;

// Importacoes da biblioteca JJWT — usada para criar e validar tokens JWT
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Servico responsavel por todas as operacoes com tokens JWT.
 *
 * O que e um JWT (JSON Web Token)?
 *   E uma string codificada com tres partes separadas por ponto:
 *     [header].[payload].[signature]
 *
 *   - header   : algoritmo usado (ex: HS256)
 *   - payload  : dados embutidos (ex: email do usuario, quando expira)
 *   - signature: assinatura gerada com a chave secreta — garante que
 *                ninguem adulterou o token
 *
 * Responsabilidades deste service:
 *   1. gerarToken(userDetails)      — cria um novo token JWT assinado
 *   2. extrairEmail(token)          — le o email embutido no token
 *   3. isTokenValido(token, user)   — verifica se o token e legitimo e nao expirou
 *
 * @Service — componente gerenciado pelo Spring (singleton).
 */
@Service
public class JwtService {

    /**
     * Chave secreta lida do application.properties via @Value.
     *
     * @Value("${jwt.secret}") — injeta o valor da propriedade jwt.secret
     * definida em application.properties. O Spring substitui a expressao
     * ${jwt.secret} pelo valor real em tempo de execucao.
     *
     * Esta chave deve ter pelo menos 256 bits (32 bytes) codificada em Base64.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Tempo de expiracao do token em milissegundos, lido do application.properties.
     * 86400000 ms = 24 horas.
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // =========================================================================
    // GERAR TOKEN
    // Cria um novo token JWT assinado para o usuario informado.
    // =========================================================================

    /**
     * Gera um token JWT para o usuario informado.
     *
     * UserDetails e a interface do Spring Security — nossa entidade Usuario
     * a implementa, entao podemos passar um Usuario diretamente aqui.
     *
     * Estrutura do token gerado:
     *   - subject (sub) : email do usuario (getUsername() retorna o email)
     *   - issuedAt  (iat): momento em que o token foi criado
     *   - expiration(exp): momento em que o token expira (agora + 24h)
     *   - assinatura     : gerada com a chave secreta usando algoritmo HS256
     *
     * @param userDetails — o usuario para quem o token sera gerado
     * @return            — a string JWT pronta para enviar ao cliente
     */
    public String gerarToken(UserDetails userDetails) {
        return Jwts.builder()
                // Define o "dono" do token — o email do usuario
                .subject(userDetails.getUsername())
                // Registra o momento de criacao
                .issuedAt(new Date(System.currentTimeMillis()))
                // Define quando o token expira
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                // Assina com a chave secreta usando HS256
                .signWith(getSigningKey())
                // Serializa tudo em uma string JWT compacta: header.payload.signature
                .compact();
    }

    // =========================================================================
    // EXTRAIR EMAIL
    // Le o email embutido no payload do token.
    // =========================================================================

    /**
     * Extrai o email (subject) armazenado dentro do token JWT.
     *
     * Claims — sao os dados embutidos no payload do token.
     * getClaims().getSubject() — retorna o campo "sub" que definimos como email.
     *
     * @param token — a string JWT recebida no header Authorization
     * @return      — o email do usuario extraido do token
     */
    public String extrairEmail(String token) {
        return getClaims(token).getSubject();
    }

    // =========================================================================
    // VALIDAR TOKEN
    // Verifica se o token e autentico e pertence ao usuario correto.
    // =========================================================================

    /**
     * Valida se o token JWT e valido para o usuario informado.
     *
     * Duas verificacoes sao feitas:
     *   1. O email dentro do token bate com o email do usuario?
     *   2. O token ainda nao expirou?
     *
     * Se ambas forem verdadeiras, o token e considerado valido e o usuario
     * pode acessar os recursos protegidos.
     *
     * @param token       — a string JWT recebida no header Authorization
     * @param userDetails — o usuario carregado do banco
     * @return            — true se o token e valido, false caso contrario
     */
    public boolean isTokenValido(String token, UserDetails userDetails) {
        // Extrai o email embutido no token
        String emailNoToken = extrairEmail(token);

        // O token e valido se:
        // 1. O email do token bate com o email do usuario carregado do banco
        // 2. O token ainda nao expirou
        return emailNoToken.equals(userDetails.getUsername()) && !isTokenExpirado(token);
    }

    // =========================================================================
    // METODOS PRIVADOS AUXILIARES
    // Detalhes internos de implementacao — outras classes nao precisam saber.
    // =========================================================================

    /**
     * Verifica se o token ja expirou.
     *
     * Compara a data de expiracao embutida no token com o momento atual.
     * Se a expiracao e anterior ao momento atual, o token expirou.
     *
     * @param token — a string JWT
     * @return      — true se o token ESTA expirado, false se ainda e valido
     */
    private boolean isTokenExpirado(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    /**
     * Extrai e retorna todos os claims (dados) embutidos no payload do token.
     *
     * Este metodo tambem valida a assinatura do token automaticamente.
     * Se a assinatura nao bater com a chave secreta, o JJWT lanca uma
     * excecao antes de retornar qualquer dado.
     *
     * @param token — a string JWT
     * @return      — objeto Claims com todos os dados do payload
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                // Informa a chave que sera usada para verificar a assinatura
                .verifyWith(getSigningKey())
                .build()
                // Analisa o token e verifica a assinatura — lanca excecao se invalido
                .parseSignedClaims(token)
                // Retorna o payload (os claims)
                .getPayload();
    }

    /**
     * Converte a chave secreta (String Base64) em um objeto SecretKey.
     *
     * O JJWT exige um objeto SecretKey para assinar e verificar tokens,
     * nao uma String diretamente. Este metodo faz a conversao:
     *   1. Decodifica a String Base64 em bytes
     *   2. Cria um SecretKey HMAC-SHA a partir dos bytes
     *
     * @return — objeto SecretKey pronto para usar com JJWT
     */
    private SecretKey getSigningKey() {
        // Decodifica a String Base64 da chave secreta em um array de bytes
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // Cria e retorna a chave HMAC-SHA a partir dos bytes
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
