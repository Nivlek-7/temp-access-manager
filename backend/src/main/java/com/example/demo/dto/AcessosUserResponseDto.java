package com.example.demo.dto;

import com.example.demo.model.Acesso;

import java.time.LocalDateTime;

public record AcessosUserResponseDto(String nomeRecurso, LocalDateTime horaPermissao, LocalDateTime horaExpiracao) {

    public static AcessosUserResponseDto from(Acesso acesso) {
        return new AcessosUserResponseDto(
                acesso.getNomeRecurso(),
                acesso.getHoraPermissao(),
                acesso.getHoraExpiracao()
        );
    }
}
