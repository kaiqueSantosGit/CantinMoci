package com.cantinmoci.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excecao lancada ao tentar cadastrar um usuario com um email que ja existe.
 *
 * @ResponseStatus(HttpStatus.CONFLICT) — HTTP 409, o status correto para
 * "a requisicao e valida, mas conflita com o estado atual do servidor"
 * (existe outro recurso com esse identificador unico).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String message) {
        super(message);
    }
}
