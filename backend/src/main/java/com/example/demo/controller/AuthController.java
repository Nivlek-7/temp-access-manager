package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Cadastro e autenticação de usuários")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    @Operation(
            summary = "Cadastra um usuário",
            description = "Endpoint público. Cria um usuário com status pendente de aprovação.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Usuário cadastrado",
                content =
                        @Content(
                                mediaType = "text/plain",
                                examples = @ExampleObject(value = "Registrado. Esperando para ser aprovado."))),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/EntradaInvalida"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflito")
    })
    public String registrar(@Valid @RequestBody RegistroRequestDto dto) {
        usuarioService.registrarUsuario(dto.nome(), dto.email(), dto.senha());
        return "Registrado. Esperando para ser aprovado.";
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autentica um usuário",
            description = "Endpoint público. Retorna um token JWT para usuários aprovados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticação realizada"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/EntradaInvalida"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    })
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto req) {
        return authService.login(req.email(), req.senha());
    }
}
