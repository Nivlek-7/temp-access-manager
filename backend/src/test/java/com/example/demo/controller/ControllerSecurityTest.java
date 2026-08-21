package com.example.demo.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.exception.ApiExceptionHandler;
import com.example.demo.exception.EmailJaCadastradoException;
import com.example.demo.model.Role;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtAuthFilter;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AcessoService;
import com.example.demo.service.AuthService;
import com.example.demo.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AuthController.class, UsuarioController.class, AcessoController.class})
@Import({SecurityConfig.class, JwtAuthFilter.class, ApiExceptionHandler.class})
class ControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private AcessoService acessoService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void cadastroELoginSaoPublicos() throws Exception {
        when(authService.login("usuario@example.com", "senha")).thenReturn(new AuthResponseDto("token-jwt", Role.USER));

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Registro("Usuário", "usuario@example.com", "senha"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Registrado. Esperando para ser aprovado."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Login("usuario@example.com", "senha"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(usuarioService).registrarUsuario("Usuário", "usuario@example.com", "senha");
        verify(authService).login("usuario@example.com", "senha");
    }

    @Test
    void usuarioComumNaoAcessaEndpointsAdministrativos() throws Exception {
        autenticar("user-token", "usuario@example.com", "USER");

        mockMvc.perform(get("/api/usuario").header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void administradorAprovaERejeitaUsuarios() throws Exception {
        autenticar("admin-token", "admin@example.com", "ADMIN");

        mockMvc.perform(post("/api/usuario/aprovar/1").header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Aprovado"));

        mockMvc.perform(post("/api/usuario/rejeitar/2").header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rejeitado"));

        verify(usuarioService).aprovar(1L);
        verify(usuarioService).rejeitar(2L);
    }

    @Test
    void requisicaoSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/usuario"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Credenciais inválidas"));
    }

    @Test
    void tokenComPerfilIncorretoRetorna403() throws Exception {
        autenticar("user-token", "usuario@example.com", "USER");

        mockMvc.perform(post("/api/usuario/aprovar/1").header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Sem permissão"));
    }

    @Test
    void payloadInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Registro("", "email-invalido", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entrada inválida"));

        verifyNoInteractions(usuarioService);
    }

    @Test
    void emailDuplicadoRetorna409() throws Exception {
        doThrow(new EmailJaCadastradoException())
                .when(usuarioService)
                .registrarUsuario("Usuário", "usuario@example.com", "senha");

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Registro("Usuário", "usuario@example.com", "senha"))))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://example.com/errors/email-ja-cadastrado"))
                .andExpect(jsonPath("$.title").value("E-mail já cadastrado"))
                .andExpect(jsonPath("$.instance").value("/api/auth/registrar"));
    }

    private void autenticar(String token, String email, String role) {
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getSubject(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(
                        User.withUsername(email).password("senha").roles(role).build());
    }

    private record Registro(String nome, String email, String senha) {}

    private record Login(String email, String senha) {}
}
