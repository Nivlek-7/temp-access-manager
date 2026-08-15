package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioStatus;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Test
    void normalizaEmailAntesDeBuscarEPersistir() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(repository.findByEmail("usuario@example.com")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuario = new UsuarioService(repository, encoder)
                .registrarUsuario("Usuário", " Usuario@Example.COM ", "senha");

        assertEquals("usuario@example.com", usuario.getEmail());
        verify(repository).findByEmail("usuario@example.com");
    }

    @Test
    void impedeAlterarStatusDefinitivo() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        Usuario aprovado = new Usuario();
        aprovado.setStatus(UsuarioStatus.APROVADO);
        when(repository.findById(1L)).thenReturn(Optional.of(aprovado));
        UsuarioService service = new UsuarioService(repository, mock(PasswordEncoder.class));

        assertThrows(RuntimeException.class, () -> service.aprovar(1L));
        assertThrows(RuntimeException.class, () -> service.rejeitar(1L));
        verify(repository, never()).save(any());
    }
}
