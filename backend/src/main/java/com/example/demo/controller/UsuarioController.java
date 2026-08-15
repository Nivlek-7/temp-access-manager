package com.example.demo.controller;

import com.example.demo.dto.UsuarioResponseDto;
import com.example.demo.model.Usuario;
import com.example.demo.service.*;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/aprovar/{id}")
    public String aprovar(@PathVariable @Positive(message = "O ID do usuário deve ser positivo") Long id) {
        usuarioService.aprovar(id);
        return "Aprovado";
    }

    @PostMapping("/rejeitar/{id}")
    public String rejeitar(@PathVariable @Positive(message = "O ID do usuário deve ser positivo") Long id) {
        usuarioService.rejeitar(id);
        return "Rejeitado";
    }

    @GetMapping
    public List<UsuarioResponseDto> listAllUsuarios() { //lista todos usuarios do tipo USER, pois os do tipo ADMIN nao sao necessario
        List<Usuario> usuarios = usuarioService.listAllUsuarios();
        List<UsuarioResponseDto> usuariosDto = usuarios.stream()
                .map(UsuarioResponseDto::from)
                .toList();
        return usuariosDto;
    }

    @GetMapping("/pendentes")
    public List<UsuarioResponseDto> listarPendentes() {
        return usuarioService.listPendentes().stream()
                .map(UsuarioResponseDto::from)
                .toList();
    }
}
