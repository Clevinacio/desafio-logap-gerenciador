package com.logap.teste.gerenciadorbackend.dto.response;

import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;

public record PedidoResumoResponse(
        Long id,
        Instant dataCriacao,
        BigDecimal valorTotal,
        StatusPedido statusPedido,
        String nomeCliente
) {
}
