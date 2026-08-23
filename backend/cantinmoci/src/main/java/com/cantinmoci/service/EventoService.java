package com.cantinmoci.service;

import com.cantinmoci.dto.AlocarEstoqueEventoDTO;
import com.cantinmoci.dto.EstoqueEventoResponseDTO;
import com.cantinmoci.dto.EventoRequestDTO;
import com.cantinmoci.dto.EventoResponseDTO;
import com.cantinmoci.dto.ProdutoVendidoDTO;
import com.cantinmoci.dto.RelatorioEventoDTO;
import com.cantinmoci.exception.OperacaoInvalidaException;
import com.cantinmoci.exception.ResourceNotFoundException;
import com.cantinmoci.model.Evento;
import com.cantinmoci.model.EstoqueEvento;
import com.cantinmoci.model.ItemVenda;
import com.cantinmoci.model.Produto;
import com.cantinmoci.model.StatusEvento;
import com.cantinmoci.model.StatusVenda;
import com.cantinmoci.model.Venda;
import com.cantinmoci.repository.EstoqueEventoRepository;
import com.cantinmoci.repository.EventoRepository;
import com.cantinmoci.repository.ProdutoRepository;
import com.cantinmoci.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service da entidade Evento — abrir/encerrar eventos, alocar estoque para
 * eles, e gerar o relatorio de vendas.
 */
@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EstoqueEventoRepository estoqueEventoRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;

    public EventoService(EventoRepository eventoRepository,
                          EstoqueEventoRepository estoqueEventoRepository,
                          ProdutoRepository produtoRepository,
                          VendaRepository vendaRepository) {
        this.eventoRepository = eventoRepository;
        this.estoqueEventoRepository = estoqueEventoRepository;
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
    }

    // =========================================================================
    // CRIAR (abrir) EVENTO
    // =========================================================================

    /**
     * Cria um evento novo, ja com status ABERTO.
     *
     * So pode existir UM evento ABERTO por vez no sistema — e essa regra
     * que permite a Venda descobrir sozinha a qual evento ela pertence
     * (VendaService.abrirVenda). Por isso recusamos criar um evento novo
     * se ja existe outro em andamento.
     */
    public EventoResponseDTO criar(EventoRequestDTO dto) {
        eventoRepository.findFirstByStatus(StatusEvento.ABERTO).ifPresent(eventoAberto -> {
            throw new OperacaoInvalidaException(
                    "Ja existe um evento aberto ('" + eventoAberto.getNome() + "'). "
                    + "Encerre-o antes de abrir um novo.");
        });

        Evento evento = new Evento();
        evento.setNome(dto.getNome());
        evento.setLocal(dto.getLocal());
        // status = ABERTO e dataAbertura = agora ja vem dos valores padrao da entidade.

        return toResponseDTO(eventoRepository.save(evento));
    }

    // =========================================================================
    // ENCERRAR EVENTO
    // =========================================================================

    /**
     * Encerra um evento — a partir daqui, nao aceita mais vendas novas nem
     * alocacao de estoque.
     *
     * Recusa encerrar se houver carrinhos (vendas ABERTA) ainda em
     * andamento neste evento — o operador precisa finalizar (ou o ADMIN
     * decidir o que fazer com eles) antes.
     */
    public EventoResponseDTO encerrar(Long id) {
        Evento evento = buscarEventoOuFalhar(id);

        if (evento.getStatus() != StatusEvento.ABERTO) {
            throw new OperacaoInvalidaException("Este evento ja esta encerrado");
        }

        List<Venda> vendasAbertas = vendaRepository.findByEventoAndStatus(evento, StatusVenda.ABERTA);
        if (!vendasAbertas.isEmpty()) {
            throw new OperacaoInvalidaException(
                    "Existem " + vendasAbertas.size() + " venda(s) em aberto neste evento. "
                    + "Finalize-as antes de encerrar.");
        }

        evento.setStatus(StatusEvento.ENCERRADO);
        evento.setDataEncerramento(LocalDateTime.now());

        return toResponseDTO(eventoRepository.save(evento));
    }

    // =========================================================================
    // BUSCAR / LISTAR
    // =========================================================================

    public EventoResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEventoOuFalhar(id));
    }

    public List<EventoResponseDTO> listar() {
        return eventoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // ALOCAR ESTOQUE PARA O EVENTO
    // =========================================================================

    /**
     * Aloca (ou reforca) estoque de um produto para um evento.
     *
     * Chamar varias vezes para o mesmo produto SOMA a quantidade — permite
     * "chegou mais Coxinha" no meio do evento, sem perder a contagem do
     * que ja tinha sido alocado e ja foi vendido.
     */
    public EstoqueEventoResponseDTO alocarEstoque(Long eventoId, AlocarEstoqueEventoDTO dto) {
        Evento evento = buscarEventoOuFalhar(eventoId);

        if (evento.getStatus() != StatusEvento.ABERTO) {
            throw new OperacaoInvalidaException("Nao e possivel alocar estoque em um evento encerrado");
        }

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto nao encontrado com id: " + dto.getProdutoId()));

        EstoqueEvento estoqueEvento = estoqueEventoRepository.findByEventoAndProduto(evento, produto)
                .orElseGet(() -> {
                    EstoqueEvento novo = new EstoqueEvento();
                    novo.setEvento(evento);
                    novo.setProduto(produto);
                    novo.setQuantidadeInicial(0);
                    novo.setQuantidadeAtual(0);
                    return novo;
                });

        estoqueEvento.setQuantidadeInicial(estoqueEvento.getQuantidadeInicial() + dto.getQuantidade());
        estoqueEvento.setQuantidadeAtual(estoqueEvento.getQuantidadeAtual() + dto.getQuantidade());

        return toEstoqueResponseDTO(estoqueEventoRepository.save(estoqueEvento));
    }

    public List<EstoqueEventoResponseDTO> listarProdutosDoEvento(Long eventoId) {
        Evento evento = buscarEventoOuFalhar(eventoId);
        return estoqueEventoRepository.findByEvento(evento).stream()
                .map(this::toEstoqueResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // RELATORIO
    // =========================================================================

    /**
     * Gera o relatorio de um evento: total arrecadado, quantidade de
     * vendas, ticket medio e ranking de produtos mais vendidos.
     *
     * So considera vendas FINALIZADA — carrinhos ainda ABERTOS nao entram
     * na contabilidade.
     */
    public RelatorioEventoDTO gerarRelatorio(Long eventoId) {
        Evento evento = buscarEventoOuFalhar(eventoId);
        List<Venda> vendasFinalizadas = vendaRepository.findByEventoAndStatus(evento, StatusVenda.FINALIZADA);

        BigDecimal totalArrecadado = BigDecimal.ZERO;

        // Acumuladores para o ranking de produtos mais vendidos. Usamos o ID
        // do produto (Long) como chave, em vez do objeto Produto — mais
        // seguro que depender de equals/hashCode de entidade JPA.
        Map<Long, String> nomesPorProduto = new LinkedHashMap<>();
        Map<Long, Integer> quantidadePorProduto = new LinkedHashMap<>();
        Map<Long, BigDecimal> valorPorProduto = new LinkedHashMap<>();

        for (Venda venda : vendasFinalizadas) {
            totalArrecadado = totalArrecadado.add(venda.getValorTotal());

            for (ItemVenda item : venda.getItens()) {
                Long produtoId = item.getProduto().getId();
                BigDecimal subtotalItem = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));

                nomesPorProduto.putIfAbsent(produtoId, item.getProduto().getNome());
                quantidadePorProduto.merge(produtoId, item.getQuantidade(), Integer::sum);
                valorPorProduto.merge(produtoId, subtotalItem, BigDecimal::add);
            }
        }

        int quantidadeVendas = vendasFinalizadas.size();

        // Cuidado com divisao por zero: se nao houver nenhuma venda ainda,
        // o ticket medio e zero, nao um erro.
        BigDecimal ticketMedio = quantidadeVendas > 0
                ? totalArrecadado.divide(BigDecimal.valueOf(quantidadeVendas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<ProdutoVendidoDTO> produtosMaisVendidos = quantidadePorProduto.entrySet().stream()
                .map(entry -> new ProdutoVendidoDTO(
                        entry.getKey(),
                        nomesPorProduto.get(entry.getKey()),
                        entry.getValue(),
                        valorPorProduto.get(entry.getKey())))
                .sorted(Comparator.comparing(ProdutoVendidoDTO::getQuantidadeVendida).reversed())
                .collect(Collectors.toList());

        return new RelatorioEventoDTO(
                evento.getId(),
                evento.getNome(),
                totalArrecadado,
                quantidadeVendas,
                ticketMedio,
                produtosMaisVendidos);
    }

    // =========================================================================
    // METODOS AUXILIARES PRIVADOS
    // =========================================================================

    private Evento buscarEventoOuFalhar(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento nao encontrado com id: " + id));
    }

    private EventoResponseDTO toResponseDTO(Evento evento) {
        return new EventoResponseDTO(
                evento.getId(),
                evento.getNome(),
                evento.getLocal(),
                evento.getStatus().name(),
                evento.getDataAbertura(),
                evento.getDataEncerramento());
    }

    private EstoqueEventoResponseDTO toEstoqueResponseDTO(EstoqueEvento estoqueEvento) {
        return new EstoqueEventoResponseDTO(
                estoqueEvento.getProduto().getId(),
                estoqueEvento.getProduto().getNome(),
                estoqueEvento.getQuantidadeInicial(),
                estoqueEvento.getQuantidadeAtual());
    }
}
