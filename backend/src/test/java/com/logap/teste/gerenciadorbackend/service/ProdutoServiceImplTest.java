package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.request.ProdutoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.ProdutoResponse;
import com.logap.teste.gerenciadorbackend.exception.BusinessException;
import com.logap.teste.gerenciadorbackend.model.ItemPedido;
import com.logap.teste.gerenciadorbackend.model.Pedido;
import com.logap.teste.gerenciadorbackend.model.Produto;
import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;
import com.logap.teste.gerenciadorbackend.repository.ItemPedidoRepository;
import com.logap.teste.gerenciadorbackend.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceImplTest {

    private ProdutoRepository produtoRepository;
    private ItemPedidoRepository itemPedidoRepository;
    private ProdutoServiceImpl produtoService;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        itemPedidoRepository = mock(ItemPedidoRepository.class);
        produtoService = new ProdutoServiceImpl(produtoRepository, itemPedidoRepository);
    }

    @Test
    void deveSalvarEMapearProduto() {
        ProdutoRequest request = new ProdutoRequest("Produto 1", "Descricao 1", new BigDecimal("10.00"), 5);
        Produto produtoSalvo = Produto.builder()
                .id(1L)
                .nome("Produto 1")
                .descricao("Descricao 1")
                .preco(new BigDecimal("10.00"))
                .quantidadeEstoque(5)
                .build();

        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoSalvo);

        ProdutoResponse response = produtoService.criarProduto(request);

        ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoRepository).save(captor.capture());
        Produto produtoEnviado = captor.getValue();

        assertEquals(request.nome(), produtoEnviado.getNome());
        assertEquals(request.descricao(), produtoEnviado.getDescricao());
        assertEquals(request.preco(), produtoEnviado.getPreco());
        assertEquals(request.quantidadeEstoque(), produtoEnviado.getQuantidadeEstoque());

        assertEquals(produtoSalvo.getId(), response.id());
        assertEquals(produtoSalvo.getNome(), response.nome());
        assertEquals(produtoSalvo.getDescricao(), response.descricao());
        assertEquals(produtoSalvo.getPreco(), response.preco());
        assertEquals(produtoSalvo.getQuantidadeEstoque(), response.quantidadeEstoque());
    }

    @Test
    void deveRetornarListaDeProdutoResponse() {
        Produto produto1 = Produto.builder()
                .id(1L)
                .nome("Produto 1")
                .descricao("Descricao 1")
                .preco(new BigDecimal("10.00"))
                .quantidadeEstoque(5)
                .build();
        Produto produto2 = Produto.builder()
                .id(2L)
                .nome("Produto 2")
                .descricao("Descricao 2")
                .preco(new BigDecimal("20.00"))
                .quantidadeEstoque(10)
                .build();

        List<Produto> produtos = Arrays.asList(produto1, produto2);
        Pageable pageable = mock(Pageable.class);
        Page<Produto> produtosPage = new org.springframework.data.domain.PageImpl<>(produtos, pageable, produtos.size());

        when(produtoRepository.findAll(pageable)).thenReturn(produtosPage);

        Page<ProdutoResponse> responses = produtoService.listarProdutosPaginado(pageable);

        assertEquals(2, responses.getContent().size());
        assertEquals(produto1.getId(), responses.getContent().get(0).id());
        assertEquals(produto2.getId(), responses.getContent().get(1).id());
    }

    @Test
    void deveMapearCorretamente() {
        Produto produto = Produto.builder()
                .id(1L)
                .nome("Produto Teste")
                .descricao("Descricao Teste")
                .preco(new BigDecimal("99.99"))
                .quantidadeEstoque(50)
                .build();

        ProdutoResponse response = produtoService.mapToResponse(produto);

        assertEquals(produto.getId(), response.id());
        assertEquals(produto.getNome(), response.nome());
        assertEquals(produto.getDescricao(), response.descricao());
        assertEquals(produto.getPreco(), response.preco());
        assertEquals(produto.getQuantidadeEstoque(), response.quantidadeEstoque());
    }
    
    @Test
    void deveAtualizarEstoqueComSucesso() {
        Produto produto = Produto.builder()
                .id(1L)
                .nome("Produto Estoque")
                .descricao("Descricao Estoque")
                .preco(new BigDecimal("50.00"))
                .quantidadeEstoque(10)
                .build();

        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoResponse response = produtoService.atualizarEstoque(1L, 25);

        assertEquals(1L, response.id());
        assertEquals(25, response.quantidadeEstoque());
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontradoAoAtualizarEstoque() {
        when(produtoRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> produtoService.atualizarEstoque(99L, 10));

        assertTrue(ex.getMessage().contains("Produto não encontrado com o ID: 99"));
    }

    @Test
    void deveDeletarProdutoQuandoNaoHaPedidosEmAndamento() {
        Produto produto = Produto.builder()
                .id(10L)
                .nome("Produto Deletar")
                .descricao("Descricao Deletar")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .build();

        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(itemPedidoRepository.findByProdutoId(10L)).thenReturn(Collections.emptyList());

        produtoService.deletarProduto(10L);

        verify(produtoRepository).deleteById(10L);
    }

    @Test
    void deveLancarExcecaoAoDeletarProdutoComPedidosCriados() {
        Produto produto = Produto.builder()
                .id(20L)
                .nome("Produto Vinculado")
                .descricao("Descricao Vinculado")
                .preco(new BigDecimal("200.00"))
                .quantidadeEstoque(2)
                .build();

        Pedido pedido = Pedido.builder()
                .id(1L)
                .status(StatusPedido.EM_ANDAMENTO)
                .build();

        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));
        when(itemPedidoRepository.findByProdutoId(20L)).thenReturn(List.of(
                ItemPedido.builder().pedido(pedido).build()
        ));

        BusinessException ex = assertThrows(BusinessException.class, () -> produtoService.deletarProduto(20L));
        assertTrue(ex.getMessage().contains("Não é possível deletar o produto"));
        verify(produtoRepository, never()).deleteById(anyLong());
    }

    @Test
    void deveLancarExcecaoAoDeletarProdutoInexistente() {
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> produtoService.deletarProduto(999L));
        assertTrue(ex.getMessage().contains("Produto não encontrado com o ID: 999"));
        verify(produtoRepository, never()).deleteById(anyLong());
    }

}