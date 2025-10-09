package com.example.demo.dto;

import com.example.demo.model.Acesso;

import java.time.LocalDateTime;

public record AcessoResponseDto(Long id, String nomeRecurso, LocalDateTime horaPermissao,
                                LocalDateTime horaExpiracao, boolean revogado, Long usuarioId, String nomeUsuario ) {

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
