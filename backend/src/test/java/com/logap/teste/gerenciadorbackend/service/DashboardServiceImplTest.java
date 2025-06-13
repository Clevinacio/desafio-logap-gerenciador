package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.dashboard.ActiveCustomerDTO;
import com.logap.teste.gerenciadorbackend.dto.dashboard.DashboardStatsDTO;
import com.logap.teste.gerenciadorbackend.dto.dashboard.TopProductDTO;
import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;
import com.logap.teste.gerenciadorbackend.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardServiceImplTest {
    private PedidoRepository pedidoRepository;
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        dashboardService = new DashboardServiceImpl(pedidoRepository);
    }

    @Test
    void getDashboardStats_deveRetornarDashboardStatsDTOCorreto() {
        BigDecimal faturamentoTotal = new BigDecimal("1000.00");
        Long totalPedidos = 10L;
        Long pedidosPendentes = 2L;
        List<TopProductDTO> topProdutos = List.of(new TopProductDTO("Produto1", 5L), new TopProductDTO("Produto2", 3L));
        List<ActiveCustomerDTO> topClientes = List.of(new ActiveCustomerDTO("Cliente1", 4L), new ActiveCustomerDTO("Cliente2", 2L));

        when(pedidoRepository.findTotalFaturamento()).thenReturn(faturamentoTotal);
        when(pedidoRepository.count()).thenReturn(totalPedidos);
        when(pedidoRepository.countByStatus(StatusPedido.EM_ANDAMENTO)).thenReturn(pedidosPendentes);
        when(pedidoRepository.findTop5Produtos()).thenReturn(topProdutos);
        when(pedidoRepository.findTop5Clientes()).thenReturn(topClientes);

        DashboardStatsDTO dto = dashboardService.getDashboardStats();

        assertEquals(faturamentoTotal, dto.faturamentoTotal());
        assertEquals(totalPedidos, dto.totalPedidos());
        assertEquals(pedidosPendentes, dto.pedidosPendentes());
        assertEquals(topProdutos, dto.topProdutos());
        assertEquals(topClientes, dto.topClientes());
    }

    @Test
    void getDashboardStats_deveRetornarZeroQuandoFaturamentoNulo() {
        when(pedidoRepository.findTotalFaturamento()).thenReturn(null);
        when(pedidoRepository.count()).thenReturn(0L);
        when(pedidoRepository.countByStatus(StatusPedido.EM_ANDAMENTO)).thenReturn(0L);
        when(pedidoRepository.findTop5Produtos()).thenReturn(List.of());
        when(pedidoRepository.findTop5Clientes()).thenReturn(List.of());

        DashboardStatsDTO dto = dashboardService.getDashboardStats();

        assertEquals(BigDecimal.ZERO, dto.faturamentoTotal());
        assertEquals(0L, dto.totalPedidos());
        assertEquals(0L, dto.pedidosPendentes());
        assertTrue(dto.topProdutos().isEmpty());
        assertTrue(dto.topClientes().isEmpty());
    }
}
