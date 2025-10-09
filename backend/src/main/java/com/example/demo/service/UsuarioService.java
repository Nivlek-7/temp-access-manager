package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    public Usuario registrarUsuario(String nome, String email, String rawSenha) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email já registrado");
        }
        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(encoder.encode(rawSenha))
                .role(Role.USER)
                .status(UsuarioStatus.PENDENTE)
                .build();
        return usuarioRepository.save(usuario);
    }

    public Usuario findUsuarioByEmail(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.orElse(null);
    }

    public Usuario aprovar(Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        u.setStatus(UsuarioStatus.APROVADO);
        return usuarioRepository.save(u);
    }

    public Usuario rejeitar(Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        u.setStatus(UsuarioStatus.REJEITADO);
        return usuarioRepository.save(u);
    }

    public List<Usuario> listAllUsuarios() { // procura somente usuários aprovados do tipo USER
        return usuarioRepository.findByStatusAndRole(UsuarioStatus.APROVADO, Role.USER);
    }

    public List<Usuario> listPendentes() {
        return usuarioRepository.findByStatus(UsuarioStatus.PENDENTE);
    }
}
