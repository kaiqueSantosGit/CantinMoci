package com.cantinmoci.service;

import com.cantinmoci.dto.ProdutoRequestDTO;
import com.cantinmoci.dto.ProdutoResponseDTO;
import com.cantinmoci.exception.ResourceNotFoundException;
import com.cantinmoci.model.Produto;
import com.cantinmoci.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service da entidade Produto.
 *
 * O Service e a camada de REGRAS DE NEGOCIO.
 * Ele fica entre o Controller (que recebe a requisicao HTTP)
 * e o Repository (que fala com o banco de dados).
 *
 * Responsabilidades do Service:
 *   - Orquestrar operacoes (buscar, validar, salvar, transformar).
 *   - Converter entre entidade (Produto) e DTOs (ProdutoRequestDTO / ProdutoResponseDTO).
 *   - Lancar excecoes de negocio quando necessario.
 *   - O Controller nao deve conhecer o Repository diretamente — passa pelo Service.
 *
 * @Service — marca esta classe como um componente de servico gerenciado pelo Spring.
 * O Spring cria uma instancia unica (singleton) e a injeta onde for necessaria.
 */
@Service
public class ProdutoService {

    // O repository e injetado via construtor (injecao de dependencia).
    // Esta e a forma recomendada: facilita testes e deixa as dependencias explicitas.
    private final ProdutoRepository produtoRepository;

    // Construtor: o Spring detecta automaticamente e injeta o ProdutoRepository.
    // Nao precisamos da anotacao @Autowired quando ha um unico construtor.
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // =========================================================================
    // LISTAR ATIVOS
    // Retorna todos os produtos com ativo = true (ignora os "deletados").
    // =========================================================================

    /**
     * Lista todos os produtos ativos.
     *
     * .stream()              — converte a lista em um "fluxo" de dados para processar.
     * .map(this::toResponseDTO) — para cada produto, chama o metodo toResponseDTO.
     * .collect(Collectors.toList()) — coleta os resultados e monta uma nova lista.
     *
     * O resultado e uma lista de ProdutoResponseDTO, nunca a entidade Produto diretamente.
     */
    public List<ProdutoResponseDTO> listarAtivos() {
        return produtoRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // BUSCAR POR ID
    // Busca um produto especifico. Lanca excecao se nao encontrar.
    // =========================================================================

    /**
     * Busca um produto pelo seu ID.
     *
     * findById(id) retorna um Optional<Produto> — um container que pode ou nao
     * ter um valor. E uma forma segura de lidar com "nao encontrado".
     *
     * .orElseThrow() — se o Optional estiver vazio (produto nao existe),
     * lanca a excecao fornecida. O Spring retorna HTTP 404 automaticamente.
     */
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto nao encontrado com id: " + id));
        return toResponseDTO(produto);
    }

    // =========================================================================
    // CRIAR
    // Recebe os dados do DTO, cria uma nova entidade e salva no banco.
    // =========================================================================

    /**
     * Cria um novo produto no banco de dados.
     *
     * Fluxo:
     *   1. Cria um objeto Produto vazio.
     *   2. Copia os dados do DTO para a entidade.
     *   3. Salva no banco (o ID e gerado automaticamente).
     *   4. Retorna o produto salvo como DTO de resposta (com o ID preenchido).
     *
     * Nota: nao definimos "ativo" aqui porque a entidade Produto
     * ja tem valor padrao: private Boolean ativo = true;
     */
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        // Cria uma nova instancia da entidade
        Produto produto = new Produto();

        // Copia os dados recebidos do cliente para a entidade
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setQuantidadeEmEstoque(dto.getQuantidadeEmEstoque());

        // Salva no banco e ja retorna o produto com ID gerado
        return toResponseDTO(produtoRepository.save(produto));
    }

    // =========================================================================
    // ATUALIZAR
    // Busca o produto existente, atualiza os campos e salva novamente.
    // =========================================================================

    /**
     * Atualiza os dados de um produto existente.
     *
     * Fluxo:
     *   1. Busca o produto no banco (lanca 404 se nao existir).
     *   2. Atualiza apenas os campos que o cliente enviou.
     *   3. Salva as alteracoes.
     *   4. Retorna o produto atualizado como DTO.
     *
     * Por que nao criamos um objeto novo?
     *   Porque precisamos manter o mesmo ID e o campo "ativo" intacto.
     *   Alteramos apenas nome, preco e quantidade.
     */
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        // Busca o produto existente — lanca ResourceNotFoundException se nao existir
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto nao encontrado com id: " + id));

        // Atualiza os campos com os novos valores recebidos
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setQuantidadeEmEstoque(dto.getQuantidadeEmEstoque());

        // Salva e retorna o produto atualizado
        return toResponseDTO(produtoRepository.save(produto));
    }

    // =========================================================================
    // DESATIVAR (soft delete)
    // Em vez de apagar o registro, marca o campo "ativo" como false.
    // =========================================================================

    /**
     * Desativa um produto (soft delete).
     *
     * Por que soft delete em vez de deletar do banco?
     *   - Preserva historico: vendas passadas que referenciam este produto
     *     continuam validas.
     *   - Permite reativar um produto sem perder seus dados.
     *   - Boa pratica em sistemas de gestao e financeiros.
     *
     * O produto desativado nao aparece mais no listarAtivos(),
     * mas continua existindo no banco com ativo = false.
     */
    public void desativar(Long id) {
        // Busca o produto — lanca 404 se nao existir
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto nao encontrado com id: " + id));

        // Marca como inativo (soft delete)
        produto.setAtivo(false);

        // Persiste a alteracao no banco
        produtoRepository.save(produto);
    }

    // =========================================================================
    // METODO AUXILIAR PRIVADO
    // Converte uma entidade Produto para ProdutoResponseDTO.
    // Usado por todos os metodos acima para evitar duplicacao de codigo.
    // =========================================================================

    /**
     * Converte uma entidade Produto em ProdutoResponseDTO.
     *
     * Este metodo e "private" porque e um detalhe interno do Service —
     * nenhuma outra classe precisa conhecê-lo.
     *
     * A referencia "this::toResponseDTO" usada no stream do listarAtivos
     * e uma "method reference" — uma forma concisa de passar este metodo
     * como argumento para o .map().
     */
    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getQuantidadeEmEstoque(),
                produto.getAtivo()
        );
    }
}
