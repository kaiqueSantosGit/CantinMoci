package com.cantinmoci.controller;

import com.cantinmoci.dto.ResetarSenhaDTO;
import com.cantinmoci.dto.UsuarioResponseDTO;
import com.cantinmoci.model.Usuario;
import com.cantinmoci.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para gestao de usuarios por um ADMIN (Fase 7).
 *
 * Todas as rotas aqui exigem token de ADMIN — restricao configurada no
 * SecurityConfig (.requestMatchers("/usuarios/**").hasRole("ADMIN")),
 * mesmo padrao usado em POST /auth/register.
 *
 * A logica de negocio continua no AuthService (junto com login/cadastro/
 * troca de senha) em vez de um UsuarioService separado — sao operacoes
 * pequenas o suficiente para nao justificar mais uma classe ainda.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final AuthService authService;

    public UsuarioController(AuthService authService) {
        this.authService = authService;
    }

    // GET /usuarios — lista usuarios ativos
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(authService.listarAtivos());
    }

    // DELETE /usuarios/{id} — desativa um usuario (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        authService.desativar(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    // PUT /usuarios/{id}/senha — ADMIN reseta a senha de outro usuario
    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> resetarSenha(
            @PathVariable Long id,
            @Valid @RequestBody ResetarSenhaDTO dto) {
        authService.resetarSenha(id, dto);
        return ResponseEntity.noContent().build();
    }
}
