package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.*;
import org.springframework.web.server.ResponseStatusException;
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
        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (u.getStatus() != UsuarioStatus.APROVADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O usuário precisa estar aprovado");
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
        Acesso a = acessoRepository.findById(acessoId).orElseThrow(() -> new RuntimeException("Acesso não encontrado"));
        if (a.isRevogado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O acesso já está revogado");
        }
        if (!a.getHoraExpiracao().isAfter(Instant.now(clock))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O acesso já está expirado");
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
