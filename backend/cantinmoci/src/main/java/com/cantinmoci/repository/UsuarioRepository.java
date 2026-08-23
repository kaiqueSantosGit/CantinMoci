package com.cantinmoci.repository;

import com.cantinmoci.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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

    // Verifica se ja existe um usuario com esse email.
    // Mais eficiente que findByEmail().isPresent() quando so precisamos
    // saber "existe ou nao" — o Spring Data JPA gera um SELECT COUNT/EXISTS
    // em vez de carregar a entidade inteira. Usado no cadastro, para
    // impedir dois usuarios com o mesmo email.
    boolean existsByEmail(String email);

    // Lista usuarios ativos — mesmo padrao do ProdutoRepository.findByAtivoTrue().
    // Usado por GET /usuarios (ADMIN).
    List<Usuario> findByAtivoTrue();
}
