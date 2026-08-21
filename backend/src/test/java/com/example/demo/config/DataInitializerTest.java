package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.demo.model.Role;
import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioStatus;
import com.example.demo.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

class DataInitializerTest {

    @Test
    void criaAdminDevComSenhaCodificada() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(repository.findByEmail("admin@example.local")).thenReturn(Optional.empty());
        when(encoder.encode("senha-local")).thenReturn("senha-codificada");

        new DataInitializer(repository, encoder, "Admin", "admin@example.local", "senha-local").run();

        ArgumentCaptor<Usuario> usuario = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(usuario.capture());
        assertEquals("Admin", usuario.getValue().getNome());
        assertEquals("admin@example.local", usuario.getValue().getEmail());
        assertEquals("senha-codificada", usuario.getValue().getSenha());
        assertEquals(Role.ADMIN, usuario.getValue().getRole());
        assertEquals(UsuarioStatus.APROVADO, usuario.getValue().getStatus());
    }
}
