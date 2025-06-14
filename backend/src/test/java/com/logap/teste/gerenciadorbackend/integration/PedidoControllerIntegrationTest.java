package com.logap.teste.gerenciadorbackend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logap.teste.gerenciadorbackend.dto.request.ItemPedidoRequest;
import com.logap.teste.gerenciadorbackend.dto.request.LoginRequest;
import com.logap.teste.gerenciadorbackend.dto.response.LoginResponse;
import com.logap.teste.gerenciadorbackend.dto.request.PedidoRequest;
import com.logap.teste.gerenciadorbackend.model.Pedido;
import com.logap.teste.gerenciadorbackend.model.Produto;
import com.logap.teste.gerenciadorbackend.model.Usuario;
import com.logap.teste.gerenciadorbackend.model.enums.Perfil;
import com.logap.teste.gerenciadorbackend.repository.PedidoRepository;
import com.logap.teste.gerenciadorbackend.repository.ProdutoRepository;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PedidoControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        Usuario cliente = criarClienteDeTeste();
        Produto produto = criarProdutoDeTeste("Notebook Gamer", 10);

        String token = obterTokenDeLogin(cliente.getEmail());

        ItemPedidoRequest itemRequest = new ItemPedidoRequest(produto.getId(), 2);
        PedidoRequest pedidoRequest = new PedidoRequest(List.of(itemRequest));

        mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated());

        List<Pedido> pedidosNoBanco = pedidoRepository.findAll();
        assertThat(pedidosNoBanco).hasSize(1);
        Long pedidoId = pedidosNoBanco.getFirst().getId();
        Pedido pedidoSalvo = pedidoRepository.findByIdWithItens(pedidoId).orElseThrow(
                () -> new AssertionError("Pedido não encontrado após criação")
        );

        assertThat(pedidoSalvo.getCliente().getId()).isEqualTo(cliente.getId());
        assertThat(pedidoSalvo.getItens()).hasSize(1);
        assertThat(pedidoSalvo.getItens().getFirst().getProduto().getId()).isEqualTo(produto.getId());
        assertThat(pedidoSalvo.getItens().getFirst().getQuantidade()).isEqualTo(2);

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow(
                () -> new AssertionError("Produto não encontrado após criação do pedido")
        );
        // Estoque não deve ser alterado na criação do pedido
        assertThat(produtoAtualizado.getQuantidadeEstoque()).isEqualTo(10);
    }

    @Test
    void NaodevePermitirCriarPedidoComEstoqueInsuficiente() throws Exception {
        Usuario cliente = criarClienteDeTeste();
        Produto produto = criarProdutoDeTeste("Mouse Sem Fio", 5);
        String token = obterTokenDeLogin(cliente.getEmail());

        ItemPedidoRequest itemRequest = new ItemPedidoRequest(produto.getId(), 10);
        PedidoRequest pedidoRequest = new PedidoRequest(List.of(itemRequest));

        mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarEstoqueAoFinalizarPedido() throws Exception {
        Usuario cliente = criarClienteDeTeste();
        Produto produto = criarProdutoDeTeste("Teclado Mecânico", 10);
        String token = obterTokenDeLogin(cliente.getEmail());

        // Cria pedido normalmente
        ItemPedidoRequest itemRequest = new ItemPedidoRequest(produto.getId(), 3);
        PedidoRequest pedidoRequest = new PedidoRequest(List.of(itemRequest));
        mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated());

        Pedido pedido = pedidoRepository.findAll().getFirst();
        Long pedidoId = pedido.getId();

        Usuario vendedor = criarVendedorDeTeste();
        String tokenVendedor = obterTokenDeLogin(vendedor.getEmail());

        // Atualiza status para FINALIZADO
        String body = "{\"novoStatus\":\"FINALIZADO\"}";
        mockMvc.perform(patch("/pedidos/" + pedidoId + "/status")
                        .header("Authorization", "Bearer " + tokenVendedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        assertThat(produtoAtualizado.getQuantidadeEstoque()).isEqualTo(7);
    }

    private Usuario criarVendedorDeTeste() {
        Usuario vendedor = Usuario.builder()
                .nome("Vendedor Teste")
                .email("vendedor@email.com")
                .senha(passwordEncoder.encode("senha123"))
                .perfil(Perfil.VENDEDOR)
                .dataCriacao(Instant.now())
                .build();
        return usuarioRepository.save(vendedor);
    }

    // --- Métodos Auxiliares para criar dados de teste ---

    private Usuario criarClienteDeTeste() {
        Usuario cliente = Usuario.builder()
                .nome("Cliente Teste")
                .email("cliente.teste@email.com")
                .senha(passwordEncoder.encode("senha123"))
                .perfil(Perfil.CLIENTE)
                .dataCriacao(Instant.now())
                .build();
        return usuarioRepository.save(cliente);
    }

    private Produto criarProdutoDeTeste(String nome, int estoque) {
        Produto produto = Produto.builder()
                .nome(nome)
                .descricao("Descrição do produto de teste")
                .preco(new BigDecimal("150.00"))
                .quantidadeEstoque(estoque)
                .build();
        return produtoRepository.save(produto);
    }

    private String obterTokenDeLogin(String email) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, "senha123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        return loginResponse.token();
    }
}
