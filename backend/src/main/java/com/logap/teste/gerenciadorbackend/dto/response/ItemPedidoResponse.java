package com.logap.teste.gerenciadorbackend.dto.response;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        Long id,
        String nomeProduto,
        int quantidade,
        BigDecimal precoUnitario
) {
}
