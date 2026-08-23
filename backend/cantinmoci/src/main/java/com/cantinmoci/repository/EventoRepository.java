package com.cantinmoci.repository;

import com.cantinmoci.model.Evento;
import com.cantinmoci.model.StatusEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Lista todos os eventos com o status informado.
    List<Evento> findByStatus(StatusEvento status);

    /**
     * Busca o (unico) evento ABERTO no momento.
     *
     * "findFirstBy" — mesmo se por algum motivo existisse mais de um
     * registro (nao deveria, o EventoService impede isso ao criar), retorna
     * so o primeiro em vez de dar erro. Optional porque pode nao haver
     * nenhum evento aberto.
     */
    Optional<Evento> findFirstByStatus(StatusEvento status);
}
