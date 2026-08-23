package com.cantinmoci.model;

// Importacoes JPA — mapeamento da classe para tabela no banco
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Importacoes do Spring Security — interfaces que o Spring precisa
// para tratar este objeto como um "usuario autenticavel"
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entidade JPA que representa a tabela "usuarios" no banco PostgreSQL.
 *
 * Esta classe faz duas coisas ao mesmo tempo:
 *
 * 1) E uma entidade JPA — mapeia para a tabela "usuarios" no banco.
 *    Funciona exatamente como a entidade Produto que voce ja conhece.
 *
 * 2) Implementa UserDetails do Spring Security — contrato que diz ao
 *    Spring "este objeto representa um usuario do sistema".
 *    O Spring Security usa esta interface para saber:
 *      - Qual e o identificador unico do usuario (getUsername → email)
 *      - Qual e a senha armazenada (getPassword → hash BCrypt)
 *      - Quais permissoes ele tem (getAuthorities → cargo)
 *      - Se a conta esta ativa (isEnabled → sempre true por enquanto)
 *
 * Por que implementar UserDetails na propria entidade?
 *   E uma abordagem simples e direta para projetos em aprendizado.
 *   Em sistemas maiores, pode-se separar em classes distintas.
 */
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    /**
     * Chave primaria — gerada automaticamente pelo banco (auto-increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome de exibicao do usuario.
     * Ex: "Kaique Santos"
     */
    @Column(nullable = false)
    private String nome;

    /**
     * Email do usuario — usado como login (identificador unico).
     * unique = true garante que dois usuarios nao podem ter o mesmo email.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Senha armazenada como hash BCrypt.
     * NUNCA armazenamos a senha em texto puro (plain text).
     * O BCrypt converte "senha123" em algo como "$2a$10$xyz..." de forma irreversivel.
     * Na autenticacao, comparamos o hash — nao a senha original.
     */
    @Column(nullable = false)
    private String senha;

    /**
     * Cargo do usuario — define o nivel de acesso.
     * @Enumerated(EnumType.STRING) — salva o nome do enum no banco ("ADMIN" ou "OPERADOR")
     *   em vez do indice numerico (0 ou 1). Texto e mais legivel e resistente a mudancas.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;

    /**
     * Indica se o usuario esta ativo no sistema (Fase 7).
     * Mesmo padrao de soft delete usado em Produto: desativar (DELETE
     * /usuarios/{id}) so marca este campo como false, nunca apaga o
     * registro — preserva o historico de vendas feitas por esse usuario.
     *
     * O valor padrao e true: todo usuario cadastrado comeca ativo.
     */
    @Column(nullable = false)
    private Boolean ativo = true;

    // =========================================================================
    // METODOS DA INTERFACE UserDetails
    // O Spring Security exige a implementacao destes metodos.
    // Eles sao o "contrato" que permite ao Spring tratar este objeto
    // como um usuario autenticavel.
    // =========================================================================

    /**
     * Retorna as permissoes (roles/authorities) do usuario.
     *
     * GrantedAuthority — representa uma permissao no Spring Security.
     * SimpleGrantedAuthority — implementacao basica que aceita uma String.
     *
     * Convencao do Spring Security: o prefixo "ROLE_" e necessario quando
     * usamos hasRole() nas configuracoes. Ex: cargo ADMIN → "ROLE_ADMIN".
     *
     * List.of(...) — cria uma lista imutavel com um unico elemento.
     * Um usuario tem exatamente um cargo, portanto uma authority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + cargo.name()));
    }

    /**
     * Retorna a senha armazenada (hash BCrypt).
     * O Spring Security usa este valor para comparar com a senha digitada.
     */
    @Override
    public String getPassword() {
        return senha;
    }

    /**
     * Retorna o identificador unico do usuario para autenticacao.
     * No nosso sistema, usamos o email como login — nao um "username".
     * Por isso retornamos o email aqui.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indica se a conta do usuario nao expirou.
     * Retornamos sempre true — nao implementamos expiracao de conta por enquanto.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se a conta do usuario nao esta bloqueada.
     * Retornamos sempre true — nao implementamos bloqueio por enquanto.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se as credenciais (senha) nao expiraram.
     * Retornamos sempre true — nao implementamos expiracao de senha por enquanto.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuario esta ativo/habilitado no sistema.
     *
     * Fase 7: passou a refletir o campo "ativo" de verdade, em vez de
     * sempre true. Isso e conferido em dois lugares:
     *   1. JwtAuthFilter — um usuario desativado nao consegue mais usar
     *      NENHUM token seu, mesmo um token emitido antes de ser desativado
     *      (a autenticacao so e registrada se isEnabled() for true).
     *   2. AuthService.autenticar() — confere explicitamente, porque este
     *      projeto nao usa o AuthenticationManager padrao do Spring
     *      (que checaria isEnabled() sozinho); o login e feito manualmente.
     */
    @Override
    public boolean isEnabled() {
        return ativo;
    }

    // =========================================================================
    // GETTERS E SETTERS
    // Mesmos padroes da entidade Produto — sem Lombok, metodos explicitos.
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
