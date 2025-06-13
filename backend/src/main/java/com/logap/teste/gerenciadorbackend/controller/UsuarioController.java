package com.logap.teste.gerenciadorbackend.controller;

import com.logap.teste.gerenciadorbackend.dto.request.UsuarioCreateRequest;
import com.logap.teste.gerenciadorbackend.dto.response.UsuarioResponse;
import com.logap.teste.gerenciadorbackend.dto.request.UsuarioUpdateRequest;
import com.logap.teste.gerenciadorbackend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping

    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody UsuarioCreateRequest request) {
        UsuarioResponse usuarioCriado = usuarioService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @PatchMapping("/{id}/perfil")
    public ResponseEntity<UsuarioResponse> atualizarPerfil(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateRequest request, Authentication authentication) {
        String adminEmail = authentication.getName();
        UsuarioResponse usuarioAtualizado = usuarioService.atualizarRoleUsuario(id, request, adminEmail);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id, Authentication authentication) {
        String adminEmail = authentication.getName();
        usuarioService.deletarUsuario(id, adminEmail);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
