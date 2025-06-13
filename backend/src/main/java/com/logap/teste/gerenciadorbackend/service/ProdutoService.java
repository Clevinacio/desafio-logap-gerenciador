package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.request.ProdutoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.ProdutoResponse;
import com.logap.teste.gerenciadorbackend.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProdutoService {
    ProdutoResponse criarProduto(ProdutoRequest produtoRequest);
    Page<ProdutoResponse> listarProdutosPaginado(Pageable pageable);
    ProdutoResponse mapToResponse(Produto produto);
    ProdutoResponse atualizarEstoque(Long idProduto, int novaQuantidade);
    void deletarProduto(Long idProduto);
}
