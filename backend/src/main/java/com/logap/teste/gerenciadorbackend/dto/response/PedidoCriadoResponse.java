package com.logap.teste.gerenciadorbackend.dto.response;

import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;

public record PedidoCriadoResponse(
        Long id,
        StatusPedido status
) {
}
