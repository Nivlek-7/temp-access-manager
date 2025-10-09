package com.example.demo.dto;

import com.example.demo.model.Usuario;

public record UsuarioResponseDto(Long id, String nome, String email) {

    public static UsuarioResponseDto from(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
