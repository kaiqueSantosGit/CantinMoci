package com.cantinmoci.model;

/**
 * Status possiveis de um Evento.
 *
 *   ABERTO     — o evento esta em andamento. So pode existir UM evento
 *                ABERTO por vez no sistema (regra aplicada no EventoService).
 *                Vendas novas so podem ser abertas se houver um evento ABERTO.
 *   ENCERRADO  — o evento terminou. Nao aceita mais vendas novas nem
 *                alocacao de estoque, mas o historico (vendas, relatorio)
 *                continua disponivel pra consulta.
 */
public enum StatusEvento {
    ABERTO,
    ENCERRADO
}
