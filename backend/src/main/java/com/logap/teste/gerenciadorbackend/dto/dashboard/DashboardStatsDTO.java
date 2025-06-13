package com.logap.teste.gerenciadorbackend.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsDTO(
        BigDecimal faturamentoTotal,
        Long totalPedidos,
        Long pedidosPendentes,
        List<TopProductDTO> topProdutos,
        List<ActiveCustomerDTO> topClientes
) {
}
