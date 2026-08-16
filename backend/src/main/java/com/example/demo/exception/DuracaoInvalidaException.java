package com.example.demo.exception;

public class DuracaoInvalidaException extends RuntimeException {
    public DuracaoInvalidaException() {
        super("A duração deve ser maior que zero e no máximo 30 dias.");
    }
}
