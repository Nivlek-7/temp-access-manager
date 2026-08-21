package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/acesso")
@Validated
@Tag(name = "Acessos", description = "Concessão, consulta e revogação de acessos temporários")
@SecurityRequirement(name = "bearerAuth")
public class AcessoController {

    private final AcessoService acessoService;
    private final UsuarioService usuarioService;

    public AcessoController(AcessoService acessoService, UsuarioService usuarioService) {
        this.acessoService = acessoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/permitir")
    @Operation(summary = "Concede um acesso temporário", description = "Endpoint protegido. Perfil necessário: ADMIN.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Acesso concedido",
                content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "true"))),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/EntradaInvalida"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado"),
        @ApiResponse(responseCode = "403", ref = "#/components/responses/SemPermissao"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NaoEncontrado"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflito")
    })
    public ResponseEntity<Boolean> permitir(@Valid @RequestBody PermissaoAcessoRequestDto req) {
        acessoService.darPermissao(req.usuarioId(), req.nomeRecurso(), req.duracaoSegundos());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/revogar/{idAcesso}")
    @Operation(summary = "Revoga um acesso", description = "Endpoint protegido. Perfil necessário: ADMIN.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Acesso revogado",
                content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Revogado"))),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/EntradaInvalida"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado"),
        @ApiResponse(responseCode = "403", ref = "#/components/responses/SemPermissao"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NaoEncontrado"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflito")
    })
    public String revogar(@PathVariable @Positive(message = "O ID do acesso deve ser positivo") Long idAcesso) {
        acessoService.revogar(idAcesso);
        return "Revogado";
    }

    @GetMapping
    @Operation(summary = "Lista todos os acessos", description = "Endpoint protegido. Perfil necessário: ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Acessos encontrados"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado"),
        @ApiResponse(responseCode = "403", ref = "#/components/responses/SemPermissao")
    })
    public List<AcessoResponseDto> getAllAcessos() {
        List<Acesso> allAcessos = acessoService.listAllAcessos();
        List<AcessoResponseDto> acessosDto =
                allAcessos.stream().map(AcessoResponseDto::from).toList();
        return acessosDto;
    }

    @GetMapping("/usuario")
    @Operation(
            summary = "Lista os acessos do usuário autenticado",
            description = "Endpoint protegido. Perfil necessário: USER.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Acessos do usuário encontrados"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado"),
        @ApiResponse(responseCode = "403", ref = "#/components/responses/SemPermissao"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NaoEncontrado")
    })
    public List<AcessosUserResponseDto>
            listAcessosUser() { // lista apenas acessos provenientes do email que vem do token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioService.findUsuarioByEmail(email);
        List<Acesso> acessos = acessoService.listAcessosUsuario(usuario.getId());

        List<AcessosUserResponseDto> acessosUserDto =
                acessos.stream().map(AcessosUserResponseDto::from).toList();
        return acessosUserDto;
    }
}
