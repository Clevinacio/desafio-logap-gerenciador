package com.logap.teste.gerenciadorbackend.dto.request;

import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusPedidoRequest(
        @NotNull(message = "O novo status é obrigatório")
        StatusPedido novoStatus
) {
}
