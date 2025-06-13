package com.logap.teste.gerenciadorbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logap.teste.gerenciadorbackend.configuration.SecurityConfiguration;
import com.logap.teste.gerenciadorbackend.dto.request.AtualizarEstoqueRequest;
import com.logap.teste.gerenciadorbackend.dto.request.ProdutoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.ProdutoResponse;
import com.logap.teste.gerenciadorbackend.exception.BusinessException;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import com.logap.teste.gerenciadorbackend.service.JwtService;
import com.logap.teste.gerenciadorbackend.service.ProdutoService;
import com.logap.teste.gerenciadorbackend.service.UserDetailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
@Import(SecurityConfiguration.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailServiceImpl userDetailService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ProdutoResponse produtoResponse;

    @BeforeEach
    void setUp() {
        produtoResponse = new ProdutoResponse(
                1L,
                "Produto Teste",
                "Descricao Teste",
                new BigDecimal("99.99"),
                10
        );
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void deveRetornarCreated() throws Exception {
        ProdutoRequest request = new ProdutoRequest(
                "Produto Teste",
                "Descricao Teste",
                new BigDecimal("99.99"),
                10
        );

        Mockito.when(produtoService.criarProduto(any(ProdutoRequest.class)))
                .thenReturn(produtoResponse);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(produtoResponse.id()))
                .andExpect(jsonPath("$.nome").value(produtoResponse.nome()))
                .andExpect(jsonPath("$.descricao").value(produtoResponse.descricao()))
                .andExpect(jsonPath("$.preco").value(produtoResponse.preco()))
                .andExpect(jsonPath("$.quantidadeEstoque").value(produtoResponse.quantidadeEstoque()));
    }

    @Test
    @WithMockUser
    void deveRetornarListaDeProdutos() throws Exception {
        ProdutoResponse produto1 = new ProdutoResponse(1L, "Produto 1", "Descricao 1", new BigDecimal("10.00"), 5);
        ProdutoResponse produto2 = new ProdutoResponse(2L, "Produto 2", "Descricao 2", new BigDecimal("20.00"), 10);
        List<ProdutoResponse> lista = Arrays.asList(produto1, produto2);
        Page<ProdutoResponse> produtos = new org.springframework.data.domain.PageImpl<>(lista);

        Mockito.when(produtoService.listarProdutosPaginado(any()))
                .thenReturn(produtos);

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(lista.size()))
                .andExpect(jsonPath("$.content[0].id").value(produto1.id()))
                .andExpect(jsonPath("$.content[1].id").value(produto2.id()));
    }
    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void deveAtualizarEstoqueComSucesso() throws Exception {
        Long idProduto = 1L;
        int novaQuantidade = 20;
        AtualizarEstoqueRequest request = new AtualizarEstoqueRequest(novaQuantidade);

        ProdutoResponse atualizado = new ProdutoResponse(
                idProduto,
                "Produto Teste",
                "Descricao Teste",
                new BigDecimal("99.99"),
                novaQuantidade
        );

        Mockito.when(produtoService.atualizarEstoque(idProduto, novaQuantidade)).thenReturn(atualizado);

        mockMvc.perform(
                patch("/produtos/{idProduto}/estoque", idProduto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(atualizado.id()))
                .andExpect(jsonPath("$.quantidadeEstoque").value(atualizado.quantidadeEstoque()));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void deveRetornarNotFoundQuandoProdutoNaoExisteAoAtualizarEstoque() throws Exception {
        Long idProduto = 99L;
        int novaQuantidade = 5;
        AtualizarEstoqueRequest request = new AtualizarEstoqueRequest(novaQuantidade);

        Mockito.when(produtoService.atualizarEstoque(idProduto, novaQuantidade))
                .thenThrow(new BusinessException("Produto não encontrado com o ID: " + idProduto));

        mockMvc.perform(
                patch("/produtos/{idProduto}/estoque", idProduto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void deveDeletarProdutoComSucesso() throws Exception {
        Long idProduto = 1L;

        Mockito.doNothing().when(produtoService).deletarProduto(idProduto);

        mockMvc.perform(delete("/produtos/{id}", idProduto))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void deveRetornarBadRequestAoDeletarProdutoInexistente() throws Exception {
        Long idProduto = 99L;

        Mockito.doThrow(new BusinessException("Produto não encontrado com o ID: " + idProduto))
                .when(produtoService).deletarProduto(idProduto);

        mockMvc.perform(delete("/produtos/{id}", idProduto))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void deveRetornarBadRequestAoDeletarProdutoComPedidos() throws Exception {
        Long idProduto = 2L;

        Mockito.doThrow(new BusinessException("Não é possível deletar o produto com pedidos"))
                .when(produtoService).deletarProduto(idProduto);

        mockMvc.perform(delete("/produtos/{id}", idProduto))
                .andExpect(status().isBadRequest());
    }

}