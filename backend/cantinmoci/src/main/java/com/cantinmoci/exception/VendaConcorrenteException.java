package com.cantinmoci.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excecao lancada quando o lock otimista (@Version no Produto) detecta que
 * outra venda alterou o mesmo produto entre a leitura e a gravacao, durante
 * a finalizacao de uma venda.
 *
 * O VendaService captura a excecao tecnica do Hibernate
 * (ObjectOptimisticLockingFailureException) e relanca esta, com uma
 * mensagem que faz sentido para quem esta usando a API — o cliente nao
 * precisa saber o que e "optimistic locking".
 *
 * @ResponseStatus(HttpStatus.CONFLICT) — HTTP 409, mesmo racional do
 * OperacaoInvalidaException: o servidor nao tem culpa, mas o estado mudou
 * entre a leitura e a escrita.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class VendaConcorrenteException extends RuntimeException {

    public VendaConcorrenteException(String message) {
        super(message);
    }
}
