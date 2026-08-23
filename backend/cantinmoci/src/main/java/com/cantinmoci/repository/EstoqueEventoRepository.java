package com.cantinmoci.repository;

import com.cantinmoci.model.Evento;
import com.cantinmoci.model.EstoqueEvento;
import com.cantinmoci.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueEventoRepository extends JpaRepository<EstoqueEvento, Long> {

    // Busca a linha de estoque de um produto especifico dentro de um evento
    // especifico. E a consulta central do modulo: "quanto desse produto
    // ainda tem nesse evento?"
    Optional<EstoqueEvento> findByEventoAndProduto(Evento evento, Produto produto);

    // Lista todo o estoque alocado para um evento (todos os produtos dele).
    // Usado por GET /eventos/{id}/produtos.
    List<EstoqueEvento> findByEvento(Evento evento);
}
