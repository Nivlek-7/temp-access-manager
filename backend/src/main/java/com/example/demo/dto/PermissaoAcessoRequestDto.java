package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PermissaoAcessoRequestDto(
        @NotNull(message = "O usuário é obrigatório")
        @Positive(message = "O ID do usuário deve ser positivo")
        Long usuarioId,

        @NotBlank(message = "O nome do recurso é obrigatório")
        String nomeRecurso,

        @NotNull(message = "A duração é obrigatória")
        @Positive(message = "A duração deve ser maior que zero")
        @Max(value = 2592000, message = "A duração máxima é de 30 dias")
        Long duracaoSegundos) {
}
