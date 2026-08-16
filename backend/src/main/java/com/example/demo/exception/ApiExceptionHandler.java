package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmailJaCadastradoException.class)
    ProblemDetail emailJaCadastrado(EmailJaCadastradoException ex, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, "email-ja-cadastrado", "E-mail já cadastrado", ex.getMessage(), request);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    ProblemDetail credenciaisInvalidas(CredenciaisInvalidasException ex, HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, "credenciais-invalidas", "Credenciais inválidas", ex.getMessage(), request);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    ProblemDetail usuarioNaoEncontrado(UsuarioNaoEncontradoException ex, HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "usuario-nao-encontrado", "Usuário não encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(AcessoNaoEncontradoException.class)
    ProblemDetail acessoNaoEncontrado(AcessoNaoEncontradoException ex, HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "acesso-nao-encontrado", "Acesso não encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(DuracaoInvalidaException.class)
    ProblemDetail duracaoInvalida(DuracaoInvalidaException ex, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "duracao-invalida", "Entrada inválida", ex.getMessage(), request);
    }

    @ExceptionHandler(OperacaoDeStatusInvalidaException.class)
    ProblemDetail operacaoDeStatusInvalida(OperacaoDeStatusInvalidaException ex, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, "operacao-de-status-invalida", "Operação de status inválida", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail entradaInvalida(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detail = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return problem(HttpStatus.BAD_REQUEST, "entrada-invalida", "Entrada inválida", detail, request);
    }

    @ExceptionHandler({HandlerMethodValidationException.class, ConstraintViolationException.class})
    ProblemDetail entradaInvalida(Exception ex, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "entrada-invalida", "Entrada inválida",
                "Um ou mais valores informados são inválidos.", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail naoAutenticado(AuthenticationException ex, HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, "credenciais-invalidas", "Credenciais inválidas",
                "Autenticação necessária ou inválida.", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail semPermissao(AccessDeniedException ex, HttpServletRequest request) {
        return problem(HttpStatus.FORBIDDEN, "sem-permissao", "Sem permissão",
                "Você não possui permissão para executar esta operação.", request);
    }

    private ProblemDetail problem(HttpStatus status, String type, String title, String detail,
                                  HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("https://example.com/errors/" + type));
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}
