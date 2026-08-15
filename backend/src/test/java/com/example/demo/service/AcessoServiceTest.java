package com.example.demo.service;

import com.example.demo.model.Acesso;
import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioStatus;
import com.example.demo.repository.AcessoRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AcessoServiceTest {

    @Test
    void criaAcessoEmUtcSemDependerDoRelogioReal() {
        Instant agora = Instant.parse("2026-08-15T12:00:00Z");
        AcessoRepository acessoRepository = mock(AcessoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Usuario usuario = new Usuario();
        usuario.setStatus(UsuarioStatus.APROVADO);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(acessoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Acesso acesso = new AcessoService(acessoRepository, usuarioRepository,
                Clock.fixed(agora, ZoneOffset.UTC)).darPermissao(1L, "Sistema", 90);

        assertEquals(agora, acesso.getHoraPermissao());
        assertEquals(agora.plusSeconds(90), acesso.getHoraExpiracao());
    }

    @Test
    void impedePermissaoParaUsuarioNaoAprovado() {
        AcessoRepository acessoRepository = mock(AcessoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Usuario usuario = new Usuario();
        usuario.setStatus(UsuarioStatus.PENDENTE);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        AcessoService service = new AcessoService(acessoRepository, usuarioRepository, Clock.systemUTC());

        assertThrows(RuntimeException.class, () -> service.darPermissao(1L, "Sistema", 90));
        verify(acessoRepository, never()).save(any());
    }

    @Test
    void impedeRevogarAcessoRevogadoOuExpirado() {
        Instant agora = Instant.parse("2026-08-15T12:00:00Z");
        AcessoRepository acessoRepository = mock(AcessoRepository.class);
        Acesso revogado = Acesso.builder().id(1L).revogado(true).horaExpiracao(agora.plusSeconds(60)).build();
        Acesso expirado = Acesso.builder().id(2L).horaExpiracao(agora).build();
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(revogado));
        when(acessoRepository.findById(2L)).thenReturn(Optional.of(expirado));
        AcessoService service = new AcessoService(acessoRepository, mock(UsuarioRepository.class),
                Clock.fixed(agora, ZoneOffset.UTC));

        assertThrows(RuntimeException.class, () -> service.revogar(1L));
        assertThrows(RuntimeException.class, () -> service.revogar(2L));
        verify(acessoRepository, never()).save(any());
    }
}
