package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.PermissaoAcessoRequestDto;
import com.example.demo.exception.DuracaoInvalidaException;
import com.example.demo.exception.OperacaoDeStatusInvalidaException;
import com.example.demo.model.Acesso;
import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioStatus;
import com.example.demo.repository.AcessoRepository;
import com.example.demo.repository.UsuarioRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AcessoServiceTest {

    @Mock
    private AcessoRepository acessoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void criaAcessoEmUtcSemDependerDoRelogioReal() {
        Instant agora = Instant.parse("2026-08-15T12:00:00Z");
        Usuario usuario = new Usuario();
        usuario.setStatus(UsuarioStatus.APROVADO);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(acessoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Acesso acesso = new AcessoService(acessoRepository, usuarioRepository, Clock.fixed(agora, ZoneOffset.UTC))
                .darPermissao(1L, "Sistema", 90);

        assertEquals(agora, acesso.getHoraPermissao());
        assertEquals(agora.plusSeconds(90), acesso.getHoraExpiracao());
        assertEquals(usuario, acesso.getUsuario());
        assertEquals("Sistema", acesso.getNomeRecurso());
        assertFalse(acesso.isRevogado());
        verify(acessoRepository).save(acesso);
    }

    @Test
    void rejeitaDuracaoInvalida() {
        AcessoService service = new AcessoService(acessoRepository, usuarioRepository, Clock.systemUTC());

        assertThrows(DuracaoInvalidaException.class, () -> service.darPermissao(1L, "Sistema", 0));
        assertThrows(
                DuracaoInvalidaException.class,
                () -> service.darPermissao(1L, "Sistema", PermissaoAcessoRequestDto.DURACAO_MAXIMA_SEGUNDOS + 1));
        verifyNoInteractions(usuarioRepository, acessoRepository);
    }

    @Test
    void revogaAcessoAtivo() {
        Instant agora = Instant.parse("2026-08-15T12:00:00Z");
        Acesso acesso = Acesso.builder()
                .id(1L)
                .revogado(false)
                .horaExpiracao(agora.plusSeconds(60))
                .build();
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(acesso));
        when(acessoRepository.save(acesso)).thenReturn(acesso);
        AcessoService service =
                new AcessoService(acessoRepository, usuarioRepository, Clock.fixed(agora, ZoneOffset.UTC));

        Acesso revogado = service.revogar(1L);

        assertTrue(revogado.isRevogado());
        verify(acessoRepository).save(acesso);
    }

    @Test
    void impedePermissaoParaUsuarioNaoAprovado() {
        Usuario usuario = new Usuario();
        usuario.setStatus(UsuarioStatus.PENDENTE);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        AcessoService service = new AcessoService(acessoRepository, usuarioRepository, Clock.systemUTC());

        assertThrows(OperacaoDeStatusInvalidaException.class, () -> service.darPermissao(1L, "Sistema", 90));
        verify(acessoRepository, never()).save(any());
    }

    @Test
    void impedeRevogarAcessoRevogadoOuExpirado() {
        Instant agora = Instant.parse("2026-08-15T12:00:00Z");
        Acesso revogado = Acesso.builder()
                .id(1L)
                .revogado(true)
                .horaExpiracao(agora.plusSeconds(60))
                .build();
        Acesso expirado = Acesso.builder().id(2L).horaExpiracao(agora).build();
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(revogado));
        when(acessoRepository.findById(2L)).thenReturn(Optional.of(expirado));
        AcessoService service =
                new AcessoService(acessoRepository, usuarioRepository, Clock.fixed(agora, ZoneOffset.UTC));

        assertThrows(OperacaoDeStatusInvalidaException.class, () -> service.revogar(1L));
        assertThrows(OperacaoDeStatusInvalidaException.class, () -> service.revogar(2L));
        verify(acessoRepository, never()).save(any());
    }
}
