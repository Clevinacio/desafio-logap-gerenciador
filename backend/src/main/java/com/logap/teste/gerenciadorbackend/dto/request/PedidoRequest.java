package com.logap.teste.gerenciadorbackend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PedidoRequest(
        @NotEmpty(message = "A lista de itens não pode estar vazia")
        @Valid
        List<ItemPedidoRequest> itens
) {
}
