package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.dashboard.DashboardStatsDTO;
import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;
import com.logap.teste.gerenciadorbackend.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final PedidoRepository pedidoRepository;

    public DashboardStatsDTO getDashboardStats() {
        BigDecimal faturamentoTotal = pedidoRepository.findTotalFaturamento();
        Long totalPedidos = pedidoRepository.count();
        Long pedidosPendentes = pedidoRepository.countByStatus(StatusPedido.EM_ANDAMENTO);
        var topProdutos = pedidoRepository.findTop5Produtos();
        var topClientes = pedidoRepository.findTop5Clientes();

        return new DashboardStatsDTO(
                faturamentoTotal != null ? faturamentoTotal : BigDecimal.ZERO,
                totalPedidos,
                pedidosPendentes,
                topProdutos,
                topClientes
        );
    }
}
