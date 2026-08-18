package com.cantinmoci.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excecao lancada quando uma operacao e pedida num momento em que ela nao
 * faz sentido para o estado atual do recurso. Exemplos usados no modulo de
 * Vendas:
 *   - tentar adicionar/alterar/remover item de uma venda ja FINALIZADA
 *   - tentar finalizar uma venda que nao tem nenhum item
 *   - tentar vender um produto desativado
 *
 * @ResponseStatus(HttpStatus.CONFLICT) — HTTP 409: a requisicao em si e
 * valida, mas conflita com o estado atual do recurso no servidor.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class OperacaoInvalidaException extends RuntimeException {

    public OperacaoInvalidaException(String message) {
        super(message);
    }
}
