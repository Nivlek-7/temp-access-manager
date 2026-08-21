package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PermissaoAcessoRequestDto(
        @Schema(example = "1")
        @NotNull(message = "O usuário é obrigatório")
        @Positive(message = "O ID do usuário deve ser positivo")
        Long usuarioId,

        @Schema(example = "Painel financeiro") @NotBlank(message = "O nome do recurso é obrigatório")
        String nomeRecurso,

        @Schema(example = "3600", description = "Duração em segundos, limitada a 30 dias")
        @NotNull(message = "A duração é obrigatória")
        @Positive(message = "A duração deve ser maior que zero")
        @Max(value = DURACAO_MAXIMA_SEGUNDOS, message = "A duração máxima é de 30 dias")
        Long duracaoSegundos) {

    public static final long DURACAO_MAXIMA_SEGUNDOS = 2592000;
}
