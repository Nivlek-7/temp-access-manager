package com.example.demo.dto;

import com.example.demo.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioResponseDto(
        @Schema(example = "1") Long id,
        @Schema(example = "Maria Silva") String nome,
        @Schema(example = "maria@example.com") String email) {

    public static UsuarioResponseDto from(Usuario usuario) {
        return new UsuarioResponseDto(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
