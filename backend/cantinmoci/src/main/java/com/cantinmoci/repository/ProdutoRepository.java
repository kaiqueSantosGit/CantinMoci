package com.cantinmoci.repository;

import com.cantinmoci.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio da entidade Produto.
 *
 * O que e uma interface que estende JpaRepository?
 *   - JpaRepository ja fornece, sem escrever nenhum codigo, operacoes prontas:
 *       save(entity)       — insere ou atualiza no banco
 *       findById(id)       — busca por ID, retorna Optional<Produto>
 *       findAll()          — lista todos os registros
 *       deleteById(id)     — deleta pelo ID
 *       count()            — conta quantos registros existem
 *   - Os dois parametros genericos sao: <TipoEntidade, TipoChavePrimaria>
 *     Aqui: Produto como entidade e Long como tipo do campo "id".
 *
 * @Repository — anotacao que marca esta interface como um componente
 * de acesso a dados gerenciado pelo Spring. O Spring cria automaticamente
 * uma implementacao concreta desta interface em tempo de execucao.
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /**
     * Metodo de consulta derivado (Derived Query Method).
     *
     * O Spring Data JPA le o nome do metodo e gera o SQL automaticamente:
     *   findBy    → SELECT * FROM produtos WHERE
     *   Ativo     → ativo =
     *   True      → true
     *
     * SQL gerado equivalente: SELECT * FROM produtos WHERE ativo = true
     *
     * Usamos este metodo para listar apenas produtos ativos,
     * ignorando os que foram "deletados" via soft delete.
     */
    List<Produto> findByAtivoTrue();
}
