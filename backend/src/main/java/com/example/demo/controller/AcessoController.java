package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/acesso")
public class AcessoController {

    private final AcessoService acessoService;
    private final UsuarioService usuarioService;

    public AcessoController(AcessoService acessoService, UsuarioService usuarioService) {
        this.acessoService = acessoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/permitir")
    public ResponseEntity<Boolean> permitir(@RequestBody PermissaoAcessoRequestDto req) {
        acessoService.darPermissao(req.usuarioId(), req.nomeRecurso(), req.duracaoSegundos());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/revogar/{idAcesso}")
    public String revogar(@PathVariable Long idAcesso) {
        acessoService.revogar(idAcesso);
        return "Revogado";
    }

    @GetMapping
    public List<AcessoResponseDto> getAllAcessos() {
        List<Acesso> allAcessos = acessoService.listAllAcessos();
        List<AcessoResponseDto> acessosDto = allAcessos.stream()
                .map(AcessoResponseDto::from)
                .toList();
        return acessosDto;
    }

    @GetMapping("/usuario")
    public List<AcessosUserResponseDto> listAcessosUser() { // lista apenas acessos provenientes do email que vem do token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioService.findUsuarioByEmail(email);
        List<Acesso> acessos = acessoService.listAcessosUsuario(usuario.getId());

        List<AcessosUserResponseDto> acessosUserDto = acessos.stream()
                .map(AcessosUserResponseDto::from)
                .toList();
        return acessosUserDto;
    }
}
