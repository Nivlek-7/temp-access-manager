package com.example.demo.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class PermissaoAcessoRequestDtoTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validaCamposDaPermissao() {
        assertEquals(
                3,
                validator.validate(new PermissaoAcessoRequestDto(-1L, " ", 0L)).size());
        assertEquals(
                1,
                validator
                        .validate(new PermissaoAcessoRequestDto(1L, "Sistema", 2592001L))
                        .size());
        assertEquals(
                0,
                validator
                        .validate(new PermissaoAcessoRequestDto(1L, "Sistema", 2592000L))
                        .size());
    }
}
