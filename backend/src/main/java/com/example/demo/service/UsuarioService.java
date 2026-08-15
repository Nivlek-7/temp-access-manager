package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.util.EmailUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.web.server.ResponseStatusException;

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
        email = EmailUtils.normalize(email);
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
        Optional<Usuario> usuario = usuarioRepository.findByEmail(EmailUtils.normalize(email));
        return usuario.orElse(null);
    }

    public Usuario aprovar(Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        validarPendente(u);
        u.setStatus(UsuarioStatus.APROVADO);
        return usuarioRepository.save(u);
    }

    public Usuario rejeitar(Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        validarPendente(u);
        u.setStatus(UsuarioStatus.REJEITADO);
        return usuarioRepository.save(u);
    }

    public List<Usuario> listAllUsuarios() { // procura somente usuários aprovados do tipo USER
        return usuarioRepository.findByStatusAndRole(UsuarioStatus.APROVADO, Role.USER);
    }

    public List<Usuario> listPendentes() {
        return usuarioRepository.findByStatus(UsuarioStatus.PENDENTE);
    }

    private void validarPendente(Usuario usuario) {
        if (usuario.getStatus() != UsuarioStatus.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O usuário já possui status definitivo");
        }
    }
}
