package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public String registrar(@RequestBody RegistroRequestDto dto) {
        usuarioService.registrarUsuario(dto.nome(), dto.email(), dto.senha());
        return "Registrado. Esperando para ser aprovado.";
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto req) {
        return authService.login(req.email(), req.senha());
    }
}
