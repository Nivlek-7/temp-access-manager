package com.example.demo.controller;

import com.example.demo.dto.UsuarioResponseDto;
import com.example.demo.model.Usuario;
import com.example.demo.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@Validated
@Tag(name = "Usuários", description = "Administração de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/aprovar/{id}")
    @Operation(summary = "Aprova um usuário pendente", description = "Endpoint protegido. Perfil necessário: ADMIN.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Usuário aprovado",
                content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Aprovado"))),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/EntradaInvalida"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado"),
        @ApiResponse(responseCode = "403", ref = "#/components/responses/SemPermissao"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NaoEncontrado"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflito")
    })
    public String aprovar(@PathVariable @Positive(message = "O ID do usuário deve ser positivo") Long id) {
        usuarioService.aprovar(id);
        return "Aprovado";
    }

    @PostMapping("/rejeitar/{id}")
    @Operation(summary = "Rejeita um usuário pendente", description = "Endpoint protegido. Perfil necessário: ADMIN.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Usuário rejeitado",
                content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Rejeitado"))),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/EntradaInvalida"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado"),
        @ApiResponse(responseCode = "403", ref = "#/components/responses/SemPermissao"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NaoEncontrado"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflito")
    })
    public String rejeitar(@PathVariable @Positive(message = "O ID do usuário deve ser positivo") Long id) {
        usuarioService.rejeitar(id);
        return "Rejeitado";
    }

    @GetMapping
    @Operation(summary = "Lista os usuários aprovados", description = "Endpoint protegido. Perfil necessário: ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuários encontrados"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado"),
        @ApiResponse(responseCode = "403", ref = "#/components/responses/SemPermissao")
    })
    public List<UsuarioResponseDto>
            listAllUsuarios() { // lista todos usuarios do tipo USER, pois os do tipo ADMIN nao sao necessario
        List<Usuario> usuarios = usuarioService.listAllUsuarios();
        List<UsuarioResponseDto> usuariosDto =
                usuarios.stream().map(UsuarioResponseDto::from).toList();
        return usuariosDto;
    }

    @GetMapping("/pendentes")
    @Operation(summary = "Lista os usuários pendentes", description = "Endpoint protegido. Perfil necessário: ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuários pendentes encontrados"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado"),
        @ApiResponse(responseCode = "403", ref = "#/components/responses/SemPermissao")
    })
    public List<UsuarioResponseDto> listarPendentes() {
        return usuarioService.listPendentes().stream()
                .map(UsuarioResponseDto::from)
                .toList();
    }
}
