package com.logap.teste.gerenciadorbackend.repository;

import com.logap.teste.gerenciadorbackend.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    List<ItemPedido> findByProdutoId(Long produtoId);
}
