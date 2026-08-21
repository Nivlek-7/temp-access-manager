package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.demo.model.Role;
import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioStatus;
import com.example.demo.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void permiteLoginDeUsuarioAprovado() {
        Usuario usuario = usuario(UsuarioStatus.APROVADO);
        when(repository.findByEmail("usuario@example.com")).thenReturn(Optional.of(usuario));

        UserDetails detalhes = service.loadUserByUsername(" Usuario@Example.COM ");

        assertEquals("usuario@example.com", detalhes.getUsername());
        assertEquals("senha-codificada", detalhes.getPassword());
        assertEquals("ROLE_USER", detalhes.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void rejeitaLoginDeUsuarioPendente() {
        assertLoginRecusado(UsuarioStatus.PENDENTE);
    }

    @Test
    void rejeitaLoginDeUsuarioRejeitado() {
        assertLoginRecusado(UsuarioStatus.REJEITADO);
    }

    private void assertLoginRecusado(UsuarioStatus status) {
        when(repository.findByEmail("usuario@example.com")).thenReturn(Optional.of(usuario(status)));

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("usuario@example.com"));
    }

    private Usuario usuario(UsuarioStatus status) {
        return Usuario.builder()
                .email("usuario@example.com")
                .senha("senha-codificada")
                .role(Role.USER)
                .status(status)
                .build();
    }
}
