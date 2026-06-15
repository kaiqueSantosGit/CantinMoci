package com.cantinmoci.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excecao customizada para recursos nao encontrados no banco de dados.
 *
 * O que e uma excecao customizada?
 *   - E uma classe que estende RuntimeException, criando um tipo de erro
 *     especifico para o nosso dominio de negocio.
 *   - Ao lancar esta excecao, podemos comunicar exatamente o que deu errado
 *     (ex: "Produto nao encontrado com id: 5") em vez de um erro generico.
 *
 * @ResponseStatus(HttpStatus.NOT_FOUND)
 *   - Quando esta excecao for lancada e nao tratada por um handler especifico,
 *     o Spring automaticamente retorna HTTP 404 (Not Found) ao cliente.
 *   - Isso poupa a necessidade de escrever codigo de tratamento em cada metodo.
 *
 * RuntimeException:
 *   - Nao e necessario declarar no "throws" dos metodos que a lancam.
 *   - Adequada para erros de logica de negocio (recurso inexistente).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor que recebe a mensagem de erro.
     *
     * Exemplo de uso no Service:
     *   throw new ResourceNotFoundException("Produto nao encontrado com id: " + id);
     *
     * O Spring retornara ao cliente:
     *   HTTP 404 Not Found
     *   { "message": "Produto nao encontrado com id: 5" }
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
