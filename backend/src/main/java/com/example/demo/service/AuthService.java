package com.example.demo.service;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.exception.CredenciaisInvalidasException;
import com.example.demo.exception.UsuarioNaoEncontradoException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.*;
import com.example.demo.util.EmailUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.*;
import org.springframework.security.authentication.*;

import java.util.*;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authManager, UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDto login(String email, String password) {
        email = EmailUtils.normalize(email);
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException ex) {
            throw new CredenciaisInvalidasException();
        }
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(UsuarioNaoEncontradoException::new);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usuario.getRole().name());
        return new AuthResponseDto(jwtUtil.generateToken(email, claims),  usuario.getRole());
    }
}
