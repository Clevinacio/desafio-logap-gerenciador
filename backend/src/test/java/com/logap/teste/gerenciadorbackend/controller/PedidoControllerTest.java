package com.logap.teste.gerenciadorbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logap.teste.gerenciadorbackend.configuration.SecurityConfiguration;
import com.logap.teste.gerenciadorbackend.dto.request.AtualizarStatusPedidoRequest;
import com.logap.teste.gerenciadorbackend.dto.request.ItemPedidoRequest;
import com.logap.teste.gerenciadorbackend.dto.request.PedidoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.ItemPedidoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoCriadoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoDetalhadoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoResumoResponse;
import com.logap.teste.gerenciadorbackend.exception.BusinessException;
import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import com.logap.teste.gerenciadorbackend.service.JwtService;
import com.logap.teste.gerenciadorbackend.service.PedidoService;
import com.logap.teste.gerenciadorbackend.service.UserDetailServiceImpl;
import com.logap.teste.gerenciadorbackend.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@Import(SecurityConfiguration.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailServiceImpl userDetailService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    private PedidoRequest pedidoRequest;
    private PedidoCriadoResponse pedidoCriado;

    @BeforeEach
    void setUp() {
        pedidoRequest = new PedidoRequest(
                List.of(new ItemPedidoRequest(1L, 2))
        );
        pedidoCriado = new PedidoCriadoResponse(
                123L, StatusPedido.EM_ANDAMENTO
        );
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void deveRetornarCreatedComMensagem() throws Exception {
        when(pedidoService.criarPedido(any(PedidoRequest.class), eq("cliente@email.com")))
                .thenReturn(pedidoCriado);

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(123L));
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = {"ADMINISTRADOR"})
    void deveListarTodosOsPedidosParaGestor() throws Exception {
        PedidoResumoResponse pedidoResumo = new PedidoResumoResponse(
                1L,  Instant.now(), BigDecimal.valueOf(100), StatusPedido.EM_ANDAMENTO, "Cliente 1"
        );
        when(pedidoService.listarTodosOsPedidos()).thenReturn(List.of(pedidoResumo));

        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nomeCliente").value("Cliente 1"));
    }

    @Test
    @WithMockUser(username = "cliente@email.com", roles = {"CLIENTE"})
    void deveListarPedidosDoCliente() throws Exception {
        PedidoResumoResponse pedidoResumo = new PedidoResumoResponse(
                2L,  Instant.now(), BigDecimal.valueOf(100), StatusPedido.EM_ANDAMENTO, "Cliente 2"
        );
        when(pedidoService.listarPedidosDoCliente("cliente@email.com")).thenReturn(List.of(pedidoResumo));

        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].nomeCliente").value("Cliente 2"));
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void deveObterPedidoPorId() throws Exception {
        List<ItemPedidoResponse> itens = List.of(
                new ItemPedidoResponse(1L, "Produto 1", 10, BigDecimal.valueOf(100))
        );
        PedidoDetalhadoResponse detalhado = new PedidoDetalhadoResponse(
                10L, "Cliente 3", "cliente@email.com", StatusPedido.EM_ANDAMENTO, BigDecimal.valueOf(200.00), Instant.now(), itens
        );
        when(pedidoService.buscarPedidoPorIdComPermissao(10L)).thenReturn(detalhado);

        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nomeCliente").value("Cliente 3"));
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = {"ADMINISTRADOR"})
    void deveAtualizarStatusPedidoComSucesso() throws Exception {
        Long pedidoId = 5L;
        AtualizarStatusPedidoRequest request = new AtualizarStatusPedidoRequest(StatusPedido.FINALIZADO);
        PedidoResumoResponse response = new PedidoResumoResponse(
                pedidoId, Instant.now(), BigDecimal.valueOf(150), StatusPedido.FINALIZADO, "Cliente Teste"
        );

        Mockito.when(pedidoService.atualizarStatus(pedidoId, StatusPedido.FINALIZADO)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.patch("/pedidos/{id}/status", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedidoId))
                .andExpect(jsonPath("$.statusPedido").value("FINALIZADO"));
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = {"ADMINISTRADOR"})
    void deveRetornarNotFoundQuandoPedidoNaoExisteAoAtualizarStatus() throws Exception {
        Long pedidoId = 999L;
        AtualizarStatusPedidoRequest request = new AtualizarStatusPedidoRequest(StatusPedido.FINALIZADO);

        Mockito.when(pedidoService.atualizarStatus(pedidoId, StatusPedido.FINALIZADO))
                .thenThrow(new BusinessException("Pedido com id 999 não encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/pedidos/{id}/status", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}