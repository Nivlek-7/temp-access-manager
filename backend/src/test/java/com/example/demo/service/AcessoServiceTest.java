package com.example.demo.service;

import com.example.demo.model.Acesso;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AcessoRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AcessoServiceTest {

    @Test
    void criaAcessoEmUtcSemDependerDoRelogioReal() {
        Instant agora = Instant.parse("2026-08-15T12:00:00Z");
        AcessoRepository acessoRepository = mock(AcessoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(acessoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Acesso acesso = new AcessoService(acessoRepository, usuarioRepository,
                Clock.fixed(agora, ZoneOffset.UTC)).darPermissao(1L, "Sistema", 90);

        assertEquals(agora, acesso.getHoraPermissao());
        assertEquals(agora.plusSeconds(90), acesso.getHoraExpiracao());
    }
}
