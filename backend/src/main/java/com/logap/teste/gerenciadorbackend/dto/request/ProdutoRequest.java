package com.logap.teste.gerenciadorbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProdutoRequest (
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    String descricao,

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser um valor positivo")
    BigDecimal preco,

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @Positive(message = "A quantidade em estoque deve ser um valor positivo")
    int quantidadeEstoque
){}
