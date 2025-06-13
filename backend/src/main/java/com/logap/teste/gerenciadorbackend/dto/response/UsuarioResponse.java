package com.logap.teste.gerenciadorbackend.dto.response;

import com.logap.teste.gerenciadorbackend.model.enums.Perfil;

import java.time.Instant;

public record UsuarioResponse(
    Long id,
    String nome,
    String email,
    Perfil perfil,
    Instant dataCriacao
) {
}
