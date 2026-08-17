package com.cantinmoci.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excecao customizada para falhas de autenticacao (credenciais invalidas).
 *
 * Mesmo padrao do ResourceNotFoundException: ao ser lancada e nao tratada
 * por um handler especifico, o Spring automaticamente responde HTTP 401
 * (Unauthorized) ao cliente, sem precisar de codigo extra de tratamento.
 *
 * Antes desta classe existir, o AuthService lancava RuntimeException, que
 * o Spring nao sabe traduzir para nenhum status especifico — por isso
 * caia no tratamento padrao de erro (HTTP 500 Internal Server Error),
 * mesmo o problema sendo do cliente (senha errada), nao do servidor.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    /**
     * Exemplo de uso no AuthService:
     *   throw new UnauthorizedException("Credenciais invalidas");
     *
     * O Spring respondera ao cliente:
     *   HTTP 401 Unauthorized
     *   { "message": "Credenciais invalidas" }
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
