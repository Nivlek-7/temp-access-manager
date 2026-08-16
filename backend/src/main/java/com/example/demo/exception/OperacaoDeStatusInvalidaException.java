package com.example.demo.exception;

public class OperacaoDeStatusInvalidaException extends RuntimeException {
    public OperacaoDeStatusInvalidaException(String message) {
        super(message);
    }
}
