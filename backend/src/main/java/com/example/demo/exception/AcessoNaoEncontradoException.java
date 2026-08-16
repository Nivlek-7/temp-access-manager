package com.example.demo.exception;

public class AcessoNaoEncontradoException extends RuntimeException {
    public AcessoNaoEncontradoException() {
        super("Acesso não encontrado.");
    }
}
