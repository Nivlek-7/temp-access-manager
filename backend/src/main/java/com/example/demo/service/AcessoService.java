package com.example.demo.service;

import com.example.demo.dto.PermissaoAcessoRequestDto;
import com.example.demo.exception.AcessoNaoEncontradoException;
import com.example.demo.exception.DuracaoInvalidaException;
import com.example.demo.exception.OperacaoDeStatusInvalidaException;
import com.example.demo.exception.UsuarioNaoEncontradoException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.*;
import java.time.*;
import java.util.*;

@Service
public class AcessoService {

    private final AcessoRepository acessoRepository;
    private final UsuarioRepository usuarioRepository;
    private final Clock clock;

    public AcessoService(AcessoRepository acessoRepository, UsuarioRepository usuarioRepository, Clock clock) {
        this.acessoRepository = acessoRepository;
        this.usuarioRepository = usuarioRepository;
        this.clock = clock;
    }

    public Acesso darPermissao(Long usuarioId, String nomeRecurso, long duracaoSegundos) {
        if (duracaoSegundos <= 0 || duracaoSegundos > PermissaoAcessoRequestDto.DURACAO_MAXIMA_SEGUNDOS) {
            throw new DuracaoInvalidaException();
        }
        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow(UsuarioNaoEncontradoException::new);
        if (u.getStatus() != UsuarioStatus.APROVADO) {
            throw new OperacaoDeStatusInvalidaException("O usuário precisa estar aprovado para receber uma permissão.");
        }
        Instant dataInicio = Instant.now(clock);
        Acesso a = Acesso.builder()
                .usuario(u)
                .nomeRecurso(nomeRecurso)
                .horaPermissao(dataInicio)
                .horaExpiracao(dataInicio.plusSeconds(duracaoSegundos))
                .revogado(false)
                .build();
        return acessoRepository.save(a);
    }

    public Acesso revogar(Long acessoId) {
        Acesso a = acessoRepository.findById(acessoId).orElseThrow(AcessoNaoEncontradoException::new);
        if (a.isRevogado()) {
            throw new OperacaoDeStatusInvalidaException("O acesso já está revogado.");
        }
        if (!a.getHoraExpiracao().isAfter(Instant.now(clock))) {
            throw new OperacaoDeStatusInvalidaException("O acesso já está expirado.");
        }
        a.setRevogado(true);
        return acessoRepository.save(a);
    }

    public List<Acesso> listAllAcessos() {
        return acessoRepository.findAll();
    }

    public List<Acesso> listAcessosUsuario(Long usuarioId) { // lista acessos do usuario que nao estejam revogados
        return acessoRepository.findByUsuarioIdAndRevogadoFalse(usuarioId);
    }
}
