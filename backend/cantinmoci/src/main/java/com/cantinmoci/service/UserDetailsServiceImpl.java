package com.cantinmoci.service;

import com.cantinmoci.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementacao do contrato UserDetailsService do Spring Security.
 *
 * Por que esta classe existe?
 *   O Spring Security precisa de uma forma de carregar os dados do usuario
 *   a partir de um identificador (no nosso caso, o email). Esta interface
 *   e o "ponto de integracao" entre o Spring Security e o nosso banco de dados.
 *
 * Quando e chamada?
 *   Durante a validacao de uma requisicao autenticada:
 *     1. O JwtAuthFilter extrai o email do token JWT
 *     2. Chama este service para carregar o usuario do banco
 *     3. Usa o usuario carregado para validar o token e autenticar a requisicao
 *
 * UserDetailsService — interface do Spring Security com um unico metodo:
 *   loadUserByUsername(String username) — carrega o usuario pelo identificador.
 *   Apesar do nome dizer "username", usamos o email como identificador.
 *
 * @Service — componente gerenciado pelo Spring.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repository injetado via construtor para buscar o usuario no banco
    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carrega o usuario do banco de dados a partir do email.
     *
     * O Spring Security chama este metodo internamente sempre que precisa
     * verificar quem e o usuario associado a um token JWT.
     *
     * Nossa entidade Usuario ja implementa UserDetails, entao podemos
     * retorna-la diretamente sem nenhuma conversao extra.
     *
     * @param email — o email do usuario (vem do campo "username" da interface,
     *               mas no nosso sistema equivale ao email)
     * @return      — o objeto Usuario que implementa UserDetails
     * @throws UsernameNotFoundException — se o email nao existir no banco.
     *   Esta excecao e parte do contrato da interface e o Spring Security
     *   a trata automaticamente retornando HTTP 401.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuario pelo email — lanca excecao se nao encontrar
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario nao encontrado com email: " + email));
    }
}
