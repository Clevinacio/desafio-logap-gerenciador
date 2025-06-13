package com.logap.teste.gerenciadorbackend.dto.request;

import com.logap.teste.gerenciadorbackend.model.enums.Perfil;
import jakarta.validation.constraints.NotNull;

public record UsuarioUpdateRequest(
    @NotNull(message = "Perfil é obrigatório")
    Perfil perfil
) {
}
