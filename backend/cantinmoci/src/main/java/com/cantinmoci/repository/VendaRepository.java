package com.cantinmoci.repository;

import com.cantinmoci.model.Evento;
import com.cantinmoci.model.StatusVenda;
import com.cantinmoci.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio da entidade Venda.
 *
 * Nao criamos um repository separado para ItemVenda: como ele e sempre
 * acessado atraves de uma Venda (venda.getItens()), e o cascade/orphanRemoval
 * configurados na Venda ja cuidam de salvar/apagar os itens automaticamente,
 * um repository proprio para ItemVenda nao seria usado em nenhum lugar.
 */
@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    /**
     * Lista vendas filtrando por status (ABERTA ou FINALIZADA).
     * Usado pelo endpoint GET /vendas?status=FINALIZADA, por exemplo,
     * para consultar so o historico de vendas fechadas.
     */
    List<Venda> findByStatus(StatusVenda status);

    /**
     * Usado pelo EventoService: encontrar vendas de um evento especifico
     * com um status especifico. Dois usos:
     *   - gerar relatorio: vendas FINALIZADAS de um evento
     *   - validar encerramento: existem vendas ABERTA (carrinho em
     *     andamento) neste evento? Se sim, nao deveria encerrar ainda.
     */
    List<Venda> findByEventoAndStatus(Evento evento, StatusVenda status);
}
