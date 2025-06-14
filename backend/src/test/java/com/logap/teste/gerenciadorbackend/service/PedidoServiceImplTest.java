package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.request.ItemPedidoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoCriadoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoDetalhadoResponse;
import com.logap.teste.gerenciadorbackend.dto.request.PedidoRequest;
import com.logap.teste.gerenciadorbackend.exception.BusinessException;
import com.logap.teste.gerenciadorbackend.model.ItemPedido;
import com.logap.teste.gerenciadorbackend.model.Pedido;
import com.logap.teste.gerenciadorbackend.model.Produto;
import com.logap.teste.gerenciadorbackend.model.Usuario;
import com.logap.teste.gerenciadorbackend.model.enums.Perfil;
import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;
import com.logap.teste.gerenciadorbackend.repository.PedidoRepository;
import com.logap.teste.gerenciadorbackend.repository.ProdutoRepository;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PedidoServiceImplTest {

    private PedidoRepository pedidoRepository;
    private ProdutoRepository produtoRepository;
    private UsuarioRepository usuarioRepository;
    private PedidoServiceImpl pedidoService;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        pedidoService = new PedidoServiceImpl(pedidoRepository, produtoRepository, usuarioRepository);
    }

    @Test
    void deveCriarPedidoComSucesso() {
        String email = "cliente@email.com";
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email(email)
                .perfil(Perfil.CLIENTE)
                .build();
        Produto produto = Produto.builder()
                .id(10L)
                .nome("Produto Teste")
                .preco(new BigDecimal("20.00"))
                .quantidadeEstoque(10)
                .build();

        ItemPedidoRequest itemRequest = new ItemPedidoRequest(produto.getId(), 2);
        PedidoRequest pedidoRequest = new PedidoRequest(List.of(itemRequest));

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(99L);
            return pedido;
        });

        PedidoCriadoResponse response = pedidoService.criarPedido(pedidoRequest, email);

        assertNotNull(response);
        assertEquals(99L, response.id());
    }

    @Test
    void deveRetornarErroDeClienteNaoEncontrado() {
        String email = "naoexiste@email.com";
        PedidoRequest pedidoRequest = new PedidoRequest(List.of());

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                pedidoService.criarPedido(pedidoRequest, email)
        );
        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    void deveRetornarErrodeProdutoNaoEncontrado() {
        String email = "cliente@email.com";
        Usuario usuario = Usuario.builder().id(1L).email(email).build();
        long produtoId = 99L;
        ItemPedidoRequest itemRequest = new ItemPedidoRequest(produtoId, 1);
        PedidoRequest pedidoRequest = new PedidoRequest(List.of(itemRequest));

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                pedidoService.criarPedido(pedidoRequest, email)
        );
        assertEquals("Produto não encontrado: " + produtoId, ex.getMessage());
    }

    @Test
    void naoDeveAlterarEstoqueAoCriarPedido() {
        String email = "cliente@email.com";
        Usuario usuario = Usuario.builder().id(1L).email(email).build();
        Produto produto = Produto.builder()
                .id(10L)
                .nome("Produto Teste")
                .preco(new BigDecimal("20.00"))
                .quantidadeEstoque(10)
                .build();
        ItemPedidoRequest itemRequest = new ItemPedidoRequest(produto.getId(), 2);
        PedidoRequest pedidoRequest = new PedidoRequest(List.of(itemRequest));

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(99L);
            return pedido;
        });

        pedidoService.criarPedido(pedidoRequest, email);
        // Não deve chamar produtoRepository.save
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void deveRetornarErroDeEstoqueInsuficienteAoFinalizarPedido() {
        Long pedidoId = 1L;
        Produto produto = Produto.builder()
                .id(10L)
                .nome("Produto Teste")
                .preco(new BigDecimal("20.00"))
                .quantidadeEstoque(1)
                .build();
        ItemPedido itemPedido = ItemPedido.builder()
                .produto(produto)
                .quantidade(2)
                .precoUnitario(produto.getPreco())
                .build();
        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .status(StatusPedido.EM_ANDAMENTO)
                .itens(List.of(itemPedido))
                .cliente(Usuario.builder().nome("Cliente Teste").build())
                .valorTotal(new BigDecimal("40.00"))
                .build();

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                pedidoService.atualizarStatus(pedidoId, StatusPedido.FINALIZADO)
        );
        assertEquals("Estoque insuficiente para o produto: " + produto.getNome(), ex.getMessage());
    }

    @Test
    void deveListarTodosOsPedidos() {
        Pedido pedido1 = Pedido.builder()
                .id(1L)
                .dataCriacao(Instant.now())
                .valorTotal(new BigDecimal("100.00"))
                .status(StatusPedido.EM_ANDAMENTO)
                .cliente(Usuario.builder().nome("Cliente 1").build())
                .build();
        Pedido pedido2 = Pedido.builder()
                .id(2L)
                .dataCriacao(Instant.now())
                .valorTotal(new BigDecimal("200.00"))
                .status(StatusPedido.FINALIZADO)
                .cliente(Usuario.builder().nome("Cliente 2").build())
                .build();

        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido1, pedido2));

        var lista = pedidoService.listarTodosOsPedidos();

        assertEquals(2, lista.size());
        assertEquals(pedido1.getId(), lista.get(0).id());
        assertEquals(pedido2.getId(), lista.get(1).id());
    }

    @Test
    void deveListarPedidosDoCliente() {
        String email = "cliente@email.com";
        Pedido pedido = Pedido.builder()
                .id(1L)
                .dataCriacao(Instant.now())
                .valorTotal(new BigDecimal("150.00"))
                .status(StatusPedido.EM_ANDAMENTO)
                .cliente(Usuario.builder().nome("Cliente Teste").email(email).build())
                .build();

        when(pedidoRepository.findByCliente_EmailOrderByDataCriacaoDesc(email)).thenReturn(List.of(pedido));

        var lista = pedidoService.listarPedidosDoCliente(email);

        assertEquals(1, lista.size());
        assertEquals(pedido.getId(), lista.getFirst().id());
        assertEquals("Cliente Teste", lista.getFirst().nomeCliente());
    }

    @Test
    void deveBuscarPedidoPorIdComPermissao() {
        Long pedidoId = 1L;
        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .cliente(Usuario.builder().email("cliente@email.com").build())
                .itens(List.of())
                .build();
        List<ItemPedido> itens = List.of(
                new ItemPedido(
                        10L,
                        pedido,
                        Produto.builder().nome("teste").preco(new BigDecimal("20.00")).build(),
                        2,
                        new BigDecimal("20.00")
                )
        );
        pedido.setItens(itens);

        when(pedidoRepository.findByIdWithItens(pedidoId)).thenReturn(Optional.of(pedido));

        PedidoDetalhadoResponse result = pedidoService.buscarPedidoPorIdComPermissao(pedidoId);

        assertEquals(pedidoId, result.id());
        assertEquals(result.itens().getFirst().nomeProduto(), itens.getFirst().getProduto().getNome());
    }

    @Test
    void deveRetornarErroAoBuscarPedidoPorIdInexistente() {
        Long pedidoId = 99L;
        when(pedidoRepository.findByIdWithItens(pedidoId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                pedidoService.buscarPedidoPorIdComPermissao(pedidoId)
        );
        assertEquals("Pedido com id 99 não encontrado: ", ex.getMessage());
    }
    
    @Test
    void deveAtualizarStatusComSucesso() {
        Long pedidoId = 1L;
        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .status(StatusPedido.EM_ANDAMENTO)
                .cliente(Usuario.builder().nome("Cliente Teste").build())
                .valorTotal(new BigDecimal("100.00"))
                .build();

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = pedidoService.atualizarStatus(pedidoId, StatusPedido.FINALIZADO);

        assertEquals(pedidoId, response.id());
        assertEquals(StatusPedido.FINALIZADO, response.statusPedido());
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoEncontradoAoAtualizarStatus() {
        Long pedidoId = 99L;
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                pedidoService.atualizarStatus(pedidoId, StatusPedido.FINALIZADO)
        );
        assertEquals("Pedido com id 99 não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoStatusNaoEhEmAndamento() {
        Long pedidoId = 2L;
        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .status(StatusPedido.FINALIZADO)
                .cliente(Usuario.builder().nome("Cliente Teste").build())
                .valorTotal(new BigDecimal("100.00"))
                .build();

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                pedidoService.atualizarStatus(pedidoId, StatusPedido.CANCELADO)
        );
        assertEquals("Não é possível atualizar o status de um pedido que não está em andamento", ex.getMessage());
    }

}

