package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthRequestDto(
        @Schema(example = "usuario@example.com")
        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") String email,
        @Schema(example = "senha-segura")
        @NotBlank(message = "A senha é obrigatória") String senha) {
}
