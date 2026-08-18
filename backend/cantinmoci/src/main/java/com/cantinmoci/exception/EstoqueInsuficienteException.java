package com.cantinmoci.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excecao lancada ao tentar vender mais unidades de um produto do que
 * existe em estoque — seja ao adicionar/ajustar um item no carrinho, seja
 * na revalidacao final feita em Venda.finalizar().
 *
 * @ResponseStatus(HttpStatus.BAD_REQUEST) — HTTP 400: o problema esta na
 * requisicao do cliente (pediu quantidade indisponivel), nao no servidor.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String message) {
        super(message);
    }
}
