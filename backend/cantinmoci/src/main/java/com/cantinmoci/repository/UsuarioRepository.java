package com.cantinmoci.repository;

import com.cantinmoci.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository da entidade Usuario.
 *
 * Assim como o ProdutoRepository, esta interface estende JpaRepository
 * e ganha automaticamente os metodos basicos de banco de dados:
 *   save(), findById(), findAll(), deleteById(), etc.
 *
 * O Spring Data JPA gera a implementacao concreta em tempo de execucao —
 * nos nao precisamos escrever SQL nem implementar a interface manualmente.
 *
 * Parametros do JpaRepository<Usuario, Long>:
 *   Usuario — o tipo da entidade gerenciada
 *   Long    — o tipo da chave primaria (o campo id)
 *
 * Metodo extra — findByEmail:
 *   O Spring Data JPA interpreta o nome do metodo e gera automaticamente
 *   a query SQL correspondente:
 *     "findBy" + "Email" → SELECT * FROM usuarios WHERE email = ?
 *
 *   Retorna Optional<Usuario> porque o email pode nao existir no banco.
 *   Optional e um container que pode ou nao conter um valor — e a forma
 *   segura de lidar com "nao encontrado" sem retornar null.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca um usuario pelo email.
    // Usado pelo AuthService (para autenticar) e pelo UserDetailsServiceImpl
    // (para o Spring Security carregar o usuario durante a validacao do token).
    Optional<Usuario> findByEmail(String email);
}
