package com.example.demo.dto;

import com.example.demo.model.Acesso;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record AcessosUserResponseDto(
        @Schema(example = "Painel financeiro") String nomeRecurso,
        @Schema(example = "2026-08-16T12:00:00Z") Instant horaPermissao,
        @Schema(example = "2026-08-16T13:00:00Z") Instant horaExpiracao) {

    public static AcessosUserResponseDto from(Acesso acesso) {
        return new AcessosUserResponseDto(
                acesso.getNomeRecurso(),
                acesso.getHoraPermissao(),
                acesso.getHoraExpiracao()
        );
    }
}
