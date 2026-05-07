package br.com.sergipetec.processoseletivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record SolicitacaoRequestDTO(
        @NotNull(message = "O ID do solicitante é obrigatório")
        Long solicitanteId,

        @NotNull(message = "O ID da categoria é obrigatório")
        Long categoriaId,

        @NotBlank(message = "A descrição não pode estar vazia")
        String descricao,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal valor
)
{}
