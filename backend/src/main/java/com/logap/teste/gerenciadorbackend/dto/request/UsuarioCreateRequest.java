package com.logap.teste.gerenciadorbackend.dto.request;

import com.logap.teste.gerenciadorbackend.model.enums.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record UsuarioCreateRequest(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Email é obrigatório")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    String senha,

    @NotNull(message = "Perfil é obrigatório")
    Perfil perfil
) {}
