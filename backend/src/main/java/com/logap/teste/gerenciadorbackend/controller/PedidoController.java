package com.logap.teste.gerenciadorbackend.controller;

import com.logap.teste.gerenciadorbackend.dto.request.AtualizarStatusPedidoRequest;
import com.logap.teste.gerenciadorbackend.dto.request.PedidoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoCriadoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoDetalhadoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoResumoResponse;
import com.logap.teste.gerenciadorbackend.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoCriadoResponse> criarPedido(@Valid @RequestBody PedidoRequest request, Authentication authentication) {
        String emailCliente = authentication.getName();
        PedidoCriadoResponse pedidoCriado = pedidoService.criarPedido(request, emailCliente);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoCriado);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResumoResponse>> listarPedidos(Authentication authentication) {
        String emailCliente = authentication.getName();
        boolean isGestor = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMINISTRADOR") ||
                                                                             grantedAuthority.getAuthority().equals("ROLE_VENDEDOR"));
        List<PedidoResumoResponse> pedidos;
        if(isGestor) {
            pedidos = pedidoService.listarTodosOsPedidos();
        } else {
            pedidos = pedidoService.listarPedidosDoCliente(emailCliente);
        }
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDetalhadoResponse> obterPedidoPorId(@PathVariable Long id) {
        PedidoDetalhadoResponse pedido = pedidoService.buscarPedidoPorIdComPermissao(id);
        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<PedidoResumoResponse> atualizarStatusPedido(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusPedidoRequest request) {
        PedidoResumoResponse pedidoAtualizado = pedidoService.atualizarStatus(id, request.novoStatus());
        return ResponseEntity.ok(pedidoAtualizado);
    }
}
