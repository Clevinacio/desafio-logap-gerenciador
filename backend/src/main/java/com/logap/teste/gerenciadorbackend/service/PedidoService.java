package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.response.PedidoCriadoResponse;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoDetalhadoResponse;
import com.logap.teste.gerenciadorbackend.dto.request.PedidoRequest;
import com.logap.teste.gerenciadorbackend.dto.response.PedidoResumoResponse;
import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;

import java.util.List;

public interface PedidoService {
    PedidoCriadoResponse criarPedido(PedidoRequest request, String emailUsuario);
    List<PedidoResumoResponse> listarTodosOsPedidos();
    List<PedidoResumoResponse> listarPedidosDoCliente(String emailCliente);
    PedidoDetalhadoResponse buscarPedidoPorIdComPermissao(Long idPedido);
    PedidoResumoResponse atualizarStatus(Long idPedido, StatusPedido novoStatus);
}
