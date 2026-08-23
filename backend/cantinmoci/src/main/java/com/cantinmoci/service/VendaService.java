package com.cantinmoci.service;

import com.cantinmoci.dto.AtualizarQuantidadeItemDTO;
import com.cantinmoci.dto.ItemVendaRequestDTO;
import com.cantinmoci.dto.ItemVendaResponseDTO;
import com.cantinmoci.dto.VendaResponseDTO;
import com.cantinmoci.exception.EstoqueInsuficienteException;
import com.cantinmoci.exception.OperacaoInvalidaException;
import com.cantinmoci.exception.ResourceNotFoundException;
import com.cantinmoci.exception.VendaConcorrenteException;
import com.cantinmoci.model.Evento;
import com.cantinmoci.model.EstoqueEvento;
import com.cantinmoci.model.ItemVenda;
import com.cantinmoci.model.Produto;
import com.cantinmoci.model.StatusEvento;
import com.cantinmoci.model.StatusVenda;
import com.cantinmoci.model.Usuario;
import com.cantinmoci.model.Venda;
import com.cantinmoci.repository.EstoqueEventoRepository;
import com.cantinmoci.repository.EventoRepository;
import com.cantinmoci.repository.ProdutoRepository;
import com.cantinmoci.repository.VendaRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service da entidade Venda — concentra toda a regra de negocio do carrinho
 * de compras e da finalizacao da venda.
 *
 * Fluxo geral do modulo:
 *   1. abrirVenda()         — operador abre um carrinho novo (status ABERTA)
 *   2. adicionarItem()      \
 *   3. atualizarQuantidade() > operador monta/ajusta o carrinho livremente
 *   4. removerItem()        /
 *   5. finalizar()          — fecha a venda: revalida e desconta o estoque
 */
@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final EventoRepository eventoRepository;
    private final EstoqueEventoRepository estoqueEventoRepository;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository,
                         EventoRepository eventoRepository, EstoqueEventoRepository estoqueEventoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.eventoRepository = eventoRepository;
        this.estoqueEventoRepository = estoqueEventoRepository;
    }

    // =========================================================================
    // ABRIR VENDA
    // Cria um carrinho novo (status ABERTA), vinculado ao operador logado
    // E ao evento ABERTO no momento (Fase 6).
    // =========================================================================

    /**
     * Abre um novo carrinho de venda.
     *
     * @param usuarioLogado — o operador autenticado, injetado pelo Controller
     *                        a partir do token JWT (nunca informado pelo cliente).
     *
     * A partir da Fase 6, toda venda precisa de um evento em andamento —
     * o metodo busca sozinho qual e o evento ABERTO (so pode existir um) e
     * vincula a venda a ele. O operador nao escolhe o evento manualmente.
     */
    public VendaResponseDTO abrirVenda(Usuario usuarioLogado) {
        Evento eventoAberto = eventoRepository.findFirstByStatus(StatusEvento.ABERTO)
                .orElseThrow(() -> new OperacaoInvalidaException(
                        "Nenhum evento esta aberto no momento. Abra um evento (POST /eventos) antes de iniciar vendas."));

        Venda venda = new Venda();
        venda.setUsuario(usuarioLogado);
        venda.setEvento(eventoAberto);
        // status = ABERTA, valorTotal = ZERO e dataAbertura = agora ja vem
        // preenchidos pelos valores padrao definidos na entidade Venda.
        return toResponseDTO(vendaRepository.save(venda));
    }

    // =========================================================================
    // ADICIONAR ITEM
    // Adiciona um produto ao carrinho, validando estoque disponivel.
    // =========================================================================

    public VendaResponseDTO adicionarItem(Long vendaId, ItemVendaRequestDTO dto) {
        Venda venda = buscarVendaOuFalhar(vendaId);
        validarVendaAberta(venda);

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto nao encontrado com id: " + dto.getProdutoId()));

        if (!produto.getAtivo()) {
            throw new OperacaoInvalidaException(
                    "Produto '" + produto.getNome() + "' esta desativado e nao pode ser vendido");
        }

        // Fase 6: o estoque verificado e o do EVENTO desta venda, nao mais
        // o Produto.quantidadeEmEstoque global.
        EstoqueEvento estoqueEvento = buscarEstoqueEventoOuFalhar(venda.getEvento(), produto);
        validarEstoqueDisponivel(estoqueEvento, dto.getQuantidade());

        ItemVenda item = new ItemVenda();
        item.setVenda(venda);
        item.setProduto(produto);
        item.setQuantidade(dto.getQuantidade());
        // Snapshot: guarda o preco ATUAL do produto no item. Se o preco do
        // produto mudar depois, este item continua com o valor de agora.
        item.setPrecoUnitario(produto.getPreco());

        venda.getItens().add(item);
        recalcularTotal(venda);

        // cascade = ALL no Venda.itens salva o ItemVenda novo automaticamente.
        return toResponseDTO(vendaRepository.save(venda));
    }

    // =========================================================================
    // ATUALIZAR QUANTIDADE DE UM ITEM
    // =========================================================================

    public VendaResponseDTO atualizarQuantidade(Long vendaId, Long itemId, AtualizarQuantidadeItemDTO dto) {
        Venda venda = buscarVendaOuFalhar(vendaId);
        validarVendaAberta(venda);

        ItemVenda item = buscarItemOuFalhar(venda, itemId);
        EstoqueEvento estoqueEvento = buscarEstoqueEventoOuFalhar(venda.getEvento(), item.getProduto());
        validarEstoqueDisponivel(estoqueEvento, dto.getQuantidade());

        item.setQuantidade(dto.getQuantidade());
        recalcularTotal(venda);

        return toResponseDTO(vendaRepository.save(venda));
    }

    // =========================================================================
    // REMOVER ITEM
    // =========================================================================

    public VendaResponseDTO removerItem(Long vendaId, Long itemId) {
        Venda venda = buscarVendaOuFalhar(vendaId);
        validarVendaAberta(venda);

        ItemVenda item = buscarItemOuFalhar(venda, itemId);
        // orphanRemoval = true no Venda.itens apaga o registro no banco
        // automaticamente quando ele sai desta lista e a Venda e salva.
        venda.getItens().remove(item);
        recalcularTotal(venda);

        return toResponseDTO(vendaRepository.save(venda));
    }

    // =========================================================================
    // BUSCAR / LISTAR
    // =========================================================================

    public VendaResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarVendaOuFalhar(id));
    }

    /**
     * Lista vendas. Se "status" for informado, filtra por ele
     * (ex: so o historico de vendas FINALIZADAS); se for nulo, lista todas.
     */
    public List<VendaResponseDTO> listar(StatusVenda status) {
        List<Venda> vendas = (status != null)
                ? vendaRepository.findByStatus(status)
                : vendaRepository.findAll();

        return vendas.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // FINALIZAR VENDA
    // Fecha o carrinho: revalida e desconta o estoque de cada item, muda
    // o status para FINALIZADA. A partir daqui, a venda nao pode mais ser
    // alterada.
    // =========================================================================

    /**
     * Finaliza uma venda.
     *
     * @Transactional — importante aqui (diferente dos outros metodos deste
     * service, que fazem so uma leitura seguida de um unico save()).
     * Este metodo pode salvar VARIOS produtos (um para cada item do
     * carrinho) mais a propria venda. @Transactional garante que tudo
     * aconteca como uma unidade so: se o desconto de estoque do 3º item
     * falhar, os descontos dos itens 1 e 2 sao desfeitos automaticamente
     * (rollback) — nunca ficamos com uma venda "meio finalizada".
     */
    @Transactional
    public VendaResponseDTO finalizar(Long vendaId) {
        Venda venda = buscarVendaOuFalhar(vendaId);
        validarVendaAberta(venda);

        if (venda.getItens().isEmpty()) {
            throw new OperacaoInvalidaException(
                    "Nao e possivel finalizar uma venda sem nenhum item");
        }

        try {
            // Revalida o estoque de cada produto NO EVENTO desta venda (pode
            // ter mudado desde que o item foi adicionado ao carrinho) e
            // desconta a quantidade vendida.
            for (ItemVenda item : venda.getItens()) {
                EstoqueEvento estoqueEvento = buscarEstoqueEventoOuFalhar(venda.getEvento(), item.getProduto());
                validarEstoqueDisponivel(estoqueEvento, item.getQuantidade());

                estoqueEvento.setQuantidadeAtual(estoqueEvento.getQuantidadeAtual() - item.getQuantidade());
                // Ao salvar aqui, o Hibernate confere o campo @Version do
                // EstoqueEvento — se outra venda alterou o mesmo produto
                // deste mesmo evento entre a leitura e este save(), ele
                // lanca ObjectOptimisticLockingFailureException (capturado abaixo).
                estoqueEventoRepository.save(estoqueEvento);
            }

            venda.setStatus(StatusVenda.FINALIZADA);
            venda.setDataFinalizacao(LocalDateTime.now());

            return toResponseDTO(vendaRepository.save(venda));

        } catch (ObjectOptimisticLockingFailureException e) {
            // Traduz a excecao tecnica do Hibernate para uma mensagem que
            // faz sentido para quem consome a API.
            throw new VendaConcorrenteException(
                    "Outra venda alterou o estoque de um dos produtos ao mesmo tempo. "
                    + "Confira o carrinho e tente finalizar novamente.");
        }
    }

    // =========================================================================
    // METODOS AUXILIARES PRIVADOS
    // =========================================================================

    private Venda buscarVendaOuFalhar(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda nao encontrada com id: " + id));
    }

    /**
     * Localiza um item dentro dos itens da venda informada.
     * Garante que o item pertence MESMO a esta venda (nao aceita, por
     * exemplo, o itemId de uma venda diferente).
     */
    private ItemVenda buscarItemOuFalhar(Venda venda, Long itemId) {
        return venda.getItens().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item nao encontrado com id: " + itemId + " nesta venda"));
    }

    private void validarVendaAberta(Venda venda) {
        if (venda.getStatus() != StatusVenda.ABERTA) {
            throw new OperacaoInvalidaException(
                    "Esta venda ja foi finalizada e nao pode mais ser alterada");
        }
    }

    /**
     * Busca a linha de estoque de um produto dentro do evento informado.
     * Se nao existir, significa que ninguem alocou estoque desse produto
     * pra esse evento ainda (POST /eventos/{id}/produtos) — nesse caso o
     * produto simplesmente nao pode ser vendido ali.
     */
    private EstoqueEvento buscarEstoqueEventoOuFalhar(Evento evento, Produto produto) {
        return estoqueEventoRepository.findByEventoAndProduto(evento, produto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto '" + produto.getNome() + "' nao esta disponivel no evento '"
                        + evento.getNome() + "'. Um ADMIN precisa alocar estoque dele primeiro "
                        + "(POST /eventos/" + evento.getId() + "/produtos)."));
    }

    private void validarEstoqueDisponivel(EstoqueEvento estoqueEvento, int quantidadeSolicitada) {
        if (quantidadeSolicitada > estoqueEvento.getQuantidadeAtual()) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para '" + estoqueEvento.getProduto().getNome()
                    + "' neste evento: disponivel " + estoqueEvento.getQuantidadeAtual()
                    + ", solicitado " + quantidadeSolicitada);
        }
    }

    /**
     * Soma (precoUnitario * quantidade) de todos os itens e atualiza
     * venda.valorTotal. Chamado sempre que a lista de itens muda.
     */
    private void recalcularTotal(Venda venda) {
        BigDecimal total = venda.getItens().stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        venda.setValorTotal(total);
    }

    private VendaResponseDTO toResponseDTO(Venda venda) {
        List<ItemVendaResponseDTO> itensDTO = venda.getItens().stream()
                .map(this::toItemResponseDTO)
                .collect(Collectors.toList());

        // Vendas criadas antes da Fase 6 nao tem evento — tratamos null com
        // cuidado aqui em vez de deixar um NullPointerException estourar.
        Long eventoId = venda.getEvento() != null ? venda.getEvento().getId() : null;
        String nomeEvento = venda.getEvento() != null ? venda.getEvento().getNome() : null;

        return new VendaResponseDTO(
                venda.getId(),
                venda.getStatus().name(),
                venda.getUsuario().getId(),
                venda.getUsuario().getNome(),
                eventoId,
                nomeEvento,
                itensDTO,
                venda.getValorTotal(),
                venda.getDataAbertura(),
                venda.getDataFinalizacao());
    }

    private ItemVendaResponseDTO toItemResponseDTO(ItemVenda item) {
        BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
        return new ItemVendaResponseDTO(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                subtotal);
    }
}
