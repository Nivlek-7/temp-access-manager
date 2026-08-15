package com.example.demo.dto;

import com.example.demo.model.Acesso;

import java.time.Instant;

public record AcessosUserResponseDto(String nomeRecurso, Instant horaPermissao, Instant horaExpiracao) {

    public static AcessosUserResponseDto from(Acesso acesso) {
        return new AcessosUserResponseDto(
                acesso.getNomeRecurso(),
                acesso.getHoraPermissao(),
                acesso.getHoraExpiracao()
        );
    }
}
