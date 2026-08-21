package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.model.Role;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JwtUtil;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class AuthServiceTest {

    @Test
    void fazLoginDeUsuarioAprovado() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        UsuarioRepository repository = mock(UsuarioRepository.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        Usuario usuario =
                Usuario.builder().email("usuario@example.com").role(Role.USER).build();
        when(repository.findByEmail("usuario@example.com")).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateToken(eq("usuario@example.com"), anyMap())).thenReturn("token-jwt");
        AuthService service = new AuthService(authenticationManager, repository, jwtUtil);

        AuthResponseDto resposta = service.login(" Usuario@Example.COM ", "senha");

        assertEquals("token-jwt", resposta.token());
        assertEquals(Role.USER, resposta.role());
        verify(authenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken("usuario@example.com", "senha"));
        verify(jwtUtil).generateToken("usuario@example.com", Map.of("role", "USER"));
    }
}
