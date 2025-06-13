package com.logap.teste.gerenciadorbackend.dto.response;

import java.math.BigDecimal;

public record ProdutoResponse(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    int quantidadeEstoque
) { }
