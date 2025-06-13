package com.logap.teste.gerenciadorbackend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AtualizarEstoqueRequest(
        @NotNull(message = "A nova quantidade é obrigatória")
        @PositiveOrZero(message = "A nova quantidade não pode ser negativa")
        int novaQuantidade
) {
}
