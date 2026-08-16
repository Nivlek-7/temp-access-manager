package com.example.demo.service;

import com.example.demo.exception.EmailJaCadastradoException;
import com.example.demo.exception.OperacaoDeStatusInvalidaException;
import com.example.demo.model.Role;
import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioStatus;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UsuarioService service;

    @Test
    void cadastraUsuarioPendenteComEmailNormalizadoESenhaCodificada() {
        when(repository.findByEmail("usuario@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("senha")).thenReturn("senha-codificada");
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuario = service.registrarUsuario("Usuário", " Usuario@Example.COM ", "senha");

        assertEquals("Usuário", usuario.getNome());
        assertEquals("usuario@example.com", usuario.getEmail());
        assertEquals("senha-codificada", usuario.getSenha());
        assertEquals(Role.USER, usuario.getRole());
        assertEquals(UsuarioStatus.PENDENTE, usuario.getStatus());
        verify(repository).findByEmail("usuario@example.com");
        verify(encoder).encode("senha");
        verify(repository).save(usuario);
    }

    @Test
    void rejeitaEmailDuplicado() {
        when(repository.findByEmail("usuario@example.com")).thenReturn(Optional.of(new Usuario()));

        assertThrows(EmailJaCadastradoException.class,
                () -> service.registrarUsuario("Usuário", " Usuario@Example.COM ", "senha"));
        verifyNoInteractions(encoder);
        verify(repository, never()).save(any());
    }

    @Test
    void aprovaERejeitaUsuariosPendentes() {
        Usuario paraAprovar = Usuario.builder().status(UsuarioStatus.PENDENTE).build();
        Usuario paraRejeitar = Usuario.builder().status(UsuarioStatus.PENDENTE).build();
        when(repository.findById(1L)).thenReturn(Optional.of(paraAprovar));
        when(repository.findById(2L)).thenReturn(Optional.of(paraRejeitar));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Usuario aprovado = service.aprovar(1L);
        Usuario rejeitado = service.rejeitar(2L);

        assertEquals(UsuarioStatus.APROVADO, aprovado.getStatus());
        assertEquals(UsuarioStatus.REJEITADO, rejeitado.getStatus());
        verify(repository).save(paraAprovar);
        verify(repository).save(paraRejeitar);
    }

    @Test
    void impedeAlterarStatusDefinitivo() {
        Usuario aprovado = new Usuario();
        aprovado.setStatus(UsuarioStatus.APROVADO);
        when(repository.findById(1L)).thenReturn(Optional.of(aprovado));
        assertThrows(OperacaoDeStatusInvalidaException.class, () -> service.aprovar(1L));
        assertThrows(OperacaoDeStatusInvalidaException.class, () -> service.rejeitar(1L));
        verify(repository, never()).save(any());
    }
}
