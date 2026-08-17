package com.example.demo.dto;

import com.example.demo.model.Acesso;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record AcessoResponseDto(
        @Schema(example = "10") Long id,
        @Schema(example = "Painel financeiro") String nomeRecurso,
        @Schema(example = "2026-08-16T12:00:00Z") Instant horaPermissao,
        @Schema(example = "2026-08-16T13:00:00Z") Instant horaExpiracao,
        @Schema(example = "false") boolean revogado,
        @Schema(example = "1") Long usuarioId,
        @Schema(example = "Maria Silva") String nomeUsuario) {

    public static AcessoResponseDto from(Acesso acesso) {
        return new AcessoResponseDto(
                acesso.getId(),
                acesso.getNomeRecurso(),
                acesso.getHoraPermissao(),
                acesso.getHoraExpiracao(),
                acesso.isRevogado(),
                acesso.getUsuario() != null ? acesso.getUsuario().getId() : null,
                acesso.getUsuario() != null ? acesso.getUsuario().getNome() : null
        );
    }
}
