package com.example.demo.task;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.example.demo.model.Acesso;
import com.example.demo.repository.AcessoRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class AcessoSchedulerTest {

    @Test
    void revogaAcessoExpiradoSemDependerDoRelogioReal() {
        Instant agora = Instant.parse("2026-08-15T12:00:00Z");
        AcessoRepository repository = mock(AcessoRepository.class);
        Acesso expirado = Acesso.builder().horaExpiracao(agora.minusSeconds(1)).build();
        when(repository.findByRevogadoFalseAndHoraExpiracaoBefore(agora)).thenReturn(List.of(expirado));

        new AcessoScheduler(repository, Clock.fixed(agora, ZoneOffset.UTC)).revogarAcessosExpirados();

        assertTrue(expirado.isRevogado());
        verify(repository).saveAll(List.of(expirado));
    }
}
