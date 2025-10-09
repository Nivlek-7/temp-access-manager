package com.example.demo.dto;

public record PermissaoAcessoRequestDto(Long usuarioId, String nomeRecurso, Long duracaoSegundos) {
}
