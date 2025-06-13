package com.logap.teste.gerenciadorbackend.repository;

import com.logap.teste.gerenciadorbackend.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}
