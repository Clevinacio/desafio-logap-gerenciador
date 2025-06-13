package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.request.ProdutoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.ProdutoResponse;
import com.logap.teste.gerenciadorbackend.exception.BusinessException;
import com.logap.teste.gerenciadorbackend.model.ItemPedido;
import com.logap.teste.gerenciadorbackend.model.Pedido;
import com.logap.teste.gerenciadorbackend.model.Produto;
import com.logap.teste.gerenciadorbackend.repository.ItemPedidoRepository;
import com.logap.teste.gerenciadorbackend.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService{
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    @Override
    public ProdutoResponse criarProduto(ProdutoRequest produtoRequest) {
        Produto produto = Produto.builder()
                .nome(produtoRequest.nome())
                .descricao(produtoRequest.descricao())
                .preco(produtoRequest.preco())
                .quantidadeEstoque(produtoRequest.quantidadeEstoque())
                .build();
        Produto produtoSalvo = produtoRepository.save(produto);
        return mapToResponse(produtoSalvo);
    }

    @Override
    public Page<ProdutoResponse> listarProdutosPaginado(Pageable pageable) {
        Page<Produto> produtosPage = produtoRepository.findAll(pageable);
        return produtosPage.map(this::mapToResponse);
    }

    @Override
    public ProdutoResponse mapToResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque()
        );
    }

    @Override
    @Transactional
    public ProdutoResponse atualizarEstoque(Long idProduto, int novaQuantidade) {
        Produto produto = produtoRepository.findById(idProduto).orElseThrow(
                () -> new BusinessException("Produto não encontrado com o ID: " + idProduto)
        );
        produto.setQuantidadeEstoque(novaQuantidade);
        Produto produtoAtualizado = produtoRepository.save(produto);
        return mapToResponse(produtoAtualizado);
    }

    @Override
    public void deletarProduto(Long idProduto) {
        produtoRepository.findById(idProduto).orElseThrow(
                () -> new BusinessException("Produto não encontrado com o ID: " + idProduto)
        );
        List<Pedido> pedidos = itemPedidoRepository.findByProdutoId(idProduto).stream()
                .map(ItemPedido::getPedido)
                .toList();

        if(!pedidos.isEmpty()){
                throw new BusinessException("Não é possível deletar o produto, pois existem pedidos com esse produto.");
        }

        produtoRepository.deleteById(idProduto);
    }
}
