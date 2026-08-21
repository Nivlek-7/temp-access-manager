package com.example.demo.repository;

import com.example.demo.model.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByStatus(UsuarioStatus status);

    List<Usuario> findByStatusAndRole(UsuarioStatus status, Role role);
}
