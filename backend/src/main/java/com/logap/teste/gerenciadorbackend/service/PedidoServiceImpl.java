package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.request.PedidoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.ItemPedidoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoCriadoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoDetalhadoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoResumoResponse;
import com.logap.teste.gerenciadorbackend.exception.BusinessException;
import com.logap.teste.gerenciadorbackend.model.ItemPedido;
import com.logap.teste.gerenciadorbackend.model.Pedido;
import com.logap.teste.gerenciadorbackend.model.Produto;
import com.logap.teste.gerenciadorbackend.model.Usuario;
import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;
import com.logap.teste.gerenciadorbackend.repository.PedidoRepository;
import com.logap.teste.gerenciadorbackend.repository.ProdutoRepository;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public PedidoCriadoResponse criarPedido(PedidoRequest request, String emailUsuario) {
        Usuario cliente = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .status(StatusPedido.EM_ANDAMENTO)
                .build();

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (var itemRequest : request.itens()) {
            Produto produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(() -> new BusinessException("Produto não encontrado: " + itemRequest.produtoId()));

            ItemPedido itemPedido = ItemPedido.builder()
                    .produto(produto)
                    .quantidade(itemRequest.quantidade())
                    .precoUnitario(produto.getPreco())
                    .build();

            pedido.adicionarItem(itemPedido);

            valorTotal = valorTotal.add(produto.getPreco().multiply(BigDecimal.valueOf(itemRequest.quantidade())));
        }

        pedido.setValorTotal(valorTotal);

        return mapToPedidoCriadoResponse(pedidoRepository.save(pedido));
    }

    private PedidoCriadoResponse mapToPedidoCriadoResponse(Pedido save) {
        return new PedidoCriadoResponse(
                save.getId(),
                save.getStatus()
        );
    }

    @Override
    public List<PedidoResumoResponse> listarTodosOsPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::mapToResumoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoResumoResponse> listarPedidosDoCliente(String emailCliente) {
        return pedidoRepository.findByCliente_EmailOrderByDataCriacaoDesc(emailCliente)
                .stream()
                .map(this::mapToResumoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PostAuthorize("returnObject.emailCliente == authentication.name or hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public PedidoDetalhadoResponse buscarPedidoPorIdComPermissao(Long idPedido) {
        Pedido pedidoEncontrado = pedidoRepository.findByIdWithItens(idPedido)
                .orElseThrow(() -> new BusinessException("Pedido com id " + idPedido +" não encontrado: "));
        return mapToPedidoDetalhadoResponse(pedidoEncontrado);
    }

    @Override
    @Transactional
    public PedidoResumoResponse atualizarStatus(Long idPedido, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new BusinessException("Pedido com id " + idPedido + " não encontrado"));

       if(!pedido.getStatus().equals(StatusPedido.EM_ANDAMENTO)) {
           throw new BusinessException("Não é possível atualizar o status de um pedido que não está em andamento");
       }

       // Se o novo status for FINALIZADO, decrementar o estoque
       if (novoStatus == StatusPedido.FINALIZADO) {
           for (ItemPedido item : pedido.getItens()) {
               Produto produto = item.getProduto();
               if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                   throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome());
               }
               produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
               produtoRepository.save(produto);
           }
       }

       pedido.setStatus(novoStatus);
       Pedido pedidoAtualizado = pedidoRepository.save(pedido);
       return mapToResumoResponse(pedidoAtualizado);
    }

    private PedidoResumoResponse mapToResumoResponse(Pedido pedido) {
        return new PedidoResumoResponse(
                pedido.getId(),
                pedido.getDataCriacao(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getCliente().getNome()
        );
    }

    private PedidoDetalhadoResponse mapToPedidoDetalhadoResponse(Pedido pedidoEncontrado) {
        List<ItemPedidoResponse> itens = pedidoEncontrado.getItens()
                .stream()
                .map(item -> new ItemPedidoResponse(
                        item.getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitario()))
                .toList();

        return new PedidoDetalhadoResponse(
                pedidoEncontrado.getId(),
                pedidoEncontrado.getCliente().getNome(),
                pedidoEncontrado.getCliente().getEmail(),
                pedidoEncontrado.getStatus(),
                pedidoEncontrado.getValorTotal(),
                pedidoEncontrado.getDataCriacao(),
                itens
        );
    }
}
