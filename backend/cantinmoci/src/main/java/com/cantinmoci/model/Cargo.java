package com.cantinmoci.model;

/**
 * Enum que representa os cargos possiveis de um usuario no sistema.
 *
 * Um enum (enumeracao) e um tipo especial em Java que define um conjunto
 * fixo de constantes. Em vez de guardar "ADMIN" como uma String qualquer
 * (o que permitiria valores errados como "ADMIn" ou "administrador"),
 * o enum garante que so existam os valores definidos aqui.
 *
 * O JPA armazena o nome do enum como texto no banco de dados (ex: "ADMIN").
 * Isso e configurado no campo cargo da entidade Usuario com @Enumerated.
 *
 * Valores:
 *   ADMIN    — acesso total ao sistema (gerenciar produtos, usuarios, eventos).
 *   OPERADOR — acesso operacional (registrar vendas, consultar estoque).
 */
public enum Cargo {
    ADMIN,
    OPERADOR
}
