package com.logap.teste.gerenciadorbackend.controller;

import com.logap.teste.gerenciadorbackend.dto.request.AtualizarEstoqueRequest;
import com.logap.teste.gerenciadorbackend.dto.request.ProdutoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.ProdutoResponse;
import com.logap.teste.gerenciadorbackend.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {
    private final ProdutoService produtoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<ProdutoResponse> criarProduto(@Valid @RequestBody ProdutoRequest request) {
        ProdutoResponse produtoCriado = produtoService.criarProduto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listarTodosProdutos(Pageable pageable) {
        Page<ProdutoResponse> produtos = produtoService.listarProdutosPaginado(pageable);
        return ResponseEntity.ok(produtos);
    }

    @PatchMapping("/{idProduto}/estoque")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<ProdutoResponse> atualizarEstoque(
            @PathVariable Long idProduto,
            @Valid @RequestBody AtualizarEstoqueRequest request) {
        ProdutoResponse produtoAtualizado = produtoService.atualizarEstoque(idProduto, request.novaQuantidade());
        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content em caso de sucesso
    }
}
