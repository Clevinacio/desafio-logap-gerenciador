package com.logap.teste.gerenciadorbackend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoRequest(
        @NotNull(message = "O ID do produto não pode ser nulo")
        Long produtoId,

        @NotNull(message = "A quantidade não pode ser nula")
        @Positive(message = "A quantidade deve ser um número positivo")
        int quantidade
) {
}
