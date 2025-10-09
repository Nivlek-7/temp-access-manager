package com.example.demo.security;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import java.util.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        if (usuario.getStatus() != UsuarioStatus.APROVADO) {
            throw new UsernameNotFoundException("Usuário não aprovado");
        }
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));
        return new org.springframework.security.core.userdetails.User(usuario.getEmail(), usuario.getSenha(), authorities);
    }
}
