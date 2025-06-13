package com.logap.teste.gerenciadorbackend.dto.response;

import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PedidoDetalhadoResponse(
        Long id,
        String nomeCliente,
        String emailCliente,
        StatusPedido status,
        BigDecimal valorTotal,
        Instant dataCriacao,
        List<ItemPedidoResponse> itens
) {
}
