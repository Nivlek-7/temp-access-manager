package com.example.demo.repository;

import com.example.demo.model.*;
import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface AcessoRepository extends JpaRepository<Acesso, Long> {

    List<Acesso> findByUsuarioIdAndRevogadoFalse(Long usuarioId);

    List<Acesso> findByRevogadoFalseAndHoraExpiracaoBefore(Instant data);
}
