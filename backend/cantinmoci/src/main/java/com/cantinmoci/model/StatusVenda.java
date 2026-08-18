package com.cantinmoci.model;

/**
 * Status possiveis de uma Venda.
 *
 * Em vez de ter uma entidade "Carrinho" separada da entidade "Venda",
 * usamos este status para representar os dois momentos da mesma venda:
 *
 *   ABERTA     — o "carrinho": o operador ainda esta adicionando/ajustando
 *                itens, o estoque AINDA NAO foi descontado.
 *   FINALIZADA — a venda foi fechada: o estoque ja foi descontado e os
 *                dados nao podem mais ser alterados.
 */
public enum StatusVenda {
    ABERTA,
    FINALIZADA
}
