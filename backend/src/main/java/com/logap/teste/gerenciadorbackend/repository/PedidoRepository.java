package com.logap.teste.gerenciadorbackend.repository;

import com.logap.teste.gerenciadorbackend.dto.dashboard.ActiveCustomerDTO;
import com.logap.teste.gerenciadorbackend.dto.dashboard.TopProductDTO;
import com.logap.teste.gerenciadorbackend.model.Pedido;
import com.logap.teste.gerenciadorbackend.model.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens WHERE p.id = :id")
    Optional<Pedido> findByIdWithItens(@Param("id") Long id);
    List<Pedido> findByCliente_EmailOrderByDataCriacaoDesc(String email);
    Optional<Pedido> findById(Long id);

    //Consultas Dashboard
    // Query para calcular o faturamento total de pedidos finalizados
    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.status = 'FINALIZADO'")
    BigDecimal findTotalFaturamento();

    // Query para contar pedidos com um status específico
    long countByStatus(StatusPedido status);

    // Query para encontrar os 5 produtos mais vendidos
    @Query("SELECT new com.logap.teste.gerenciadorbackend.dto.dashboard.TopProductDTO(i.produto.nome, SUM(i.quantidade)) " +
            "FROM ItemPedido i GROUP BY i.produto.nome ORDER BY SUM(i.quantidade) DESC LIMIT 5")
    List<TopProductDTO> findTop5Produtos();

    // Query para encontrar os 5 clientes mais ativos
    @Query("SELECT new com.logap.teste.gerenciadorbackend.dto.dashboard.ActiveCustomerDTO(p.cliente.nome, COUNT(p)) " +
            "FROM Pedido p GROUP BY p.cliente.nome ORDER BY COUNT(p) DESC LIMIT 5")
    List<ActiveCustomerDTO> findTop5Clientes();

    boolean existsByClienteId(Long id);
}
