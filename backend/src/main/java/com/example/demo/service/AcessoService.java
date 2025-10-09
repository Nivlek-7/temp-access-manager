package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.*;
import java.time.*;
import java.util.*;

@Service
public class AcessoService {

    private final AcessoRepository acessoRepository;
    private final UsuarioRepository usuarioRepository;

    public AcessoService(AcessoRepository acessoRepository, UsuarioRepository usuarioRepository) {
        this.acessoRepository = acessoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Acesso darPermissao(Long usuarioId, String nomeRecurso, long duracaoSegundos) {
        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        ZonedDateTime dataInicio = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")); // para nao adicionar 3h a mais
        ZonedDateTime dataExpiracao = dataInicio.plusSeconds(duracaoSegundos);
        Acesso a = Acesso.builder()
                .usuario(u)
                .nomeRecurso(nomeRecurso)
                .horaPermissao(dataInicio.toLocalDateTime())
                .horaExpiracao(dataExpiracao.toLocalDateTime())
                .revogado(false)
                .build();
        return acessoRepository.save(a);
    }

    public Acesso revogar(Long acessoId) {
        Acesso a = acessoRepository.findById(acessoId).orElseThrow(() -> new RuntimeException("Acesso não encontrado"));
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
