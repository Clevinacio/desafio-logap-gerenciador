package com.logap.teste.gerenciadorbackend.controller;

import com.logap.teste.gerenciadorbackend.configuration.SecurityConfiguration;
import com.logap.teste.gerenciadorbackend.dto.dashboard.ActiveCustomerDTO;
import com.logap.teste.gerenciadorbackend.dto.dashboard.DashboardStatsDTO;
import com.logap.teste.gerenciadorbackend.dto.dashboard.TopProductDTO;
import com.logap.teste.gerenciadorbackend.service.DashboardService;
import com.logap.teste.gerenciadorbackend.service.JwtService;
import com.logap.teste.gerenciadorbackend.service.UserDetailServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import(SecurityConfiguration.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailServiceImpl userDetailService;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void deveRetornarDashboardDTOComSucesso() throws Exception {
        DashboardStatsDTO stats = new DashboardStatsDTO(
                BigDecimal.valueOf(10000.00),
                50L,
                10L,
                List.of(new TopProductDTO("Produto A", 20L), new TopProductDTO("Produto B", 15L)),
                List.of(new ActiveCustomerDTO("Cliente A", 5L), new ActiveCustomerDTO("Cliente B", 3L))
        );
        Mockito.when(dashboardService.getDashboardStats()).thenReturn(stats);
        mockMvc.perform(get("/dashboard/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OUTRO")
    void deveRetornar403CasoNaoPossuaPermissao() throws Exception {
        mockMvc.perform(get("/dashboard/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}