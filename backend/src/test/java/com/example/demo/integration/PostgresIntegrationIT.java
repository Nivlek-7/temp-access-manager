package com.example.demo.integration;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.model.Acesso;
import com.example.demo.model.Role;
import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioStatus;
import com.example.demo.repository.AcessoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.AcessoService;
import com.example.demo.service.AuthService;
import com.example.demo.service.UsuarioService;
import com.example.demo.task.AcessoScheduler;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(properties = {
        "jwt.secret=segredo-de-teste-com-pelo-menos-32-caracteres",
        "jwt.expiration=3600000",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@Transactional
class PostgresIntegrationIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configurarPostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AcessoRepository acessoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AcessoService acessoService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AcessoScheduler acessoScheduler;

    @Autowired
    private EntityManager entityManager;

    @Test
    void persisteMapeamentosJpaEExecutaConsultasCustomizadas() {
        Usuario aprovado = salvarUsuario("aprovado@example.com", UsuarioStatus.APROVADO);
        salvarUsuario("pendente@example.com", UsuarioStatus.PENDENTE);
        Instant inicio = Instant.parse("2026-08-15T12:00:00Z");
        Acesso ativo = acessoRepository.save(Acesso.builder()
                .usuario(aprovado)
                .nomeRecurso("Sistema")
                .horaPermissao(inicio)
                .horaExpiracao(inicio.plusSeconds(60))
                .revogado(false)
                .build());
        acessoRepository.save(Acesso.builder()
                .usuario(aprovado)
                .nomeRecurso("Expirado")
                .horaPermissao(inicio.minusSeconds(120))
                .horaExpiracao(inicio.minusSeconds(60))
                .revogado(false)
                .build());
        entityManager.flush();
        entityManager.clear();

        Acesso recarregado = acessoRepository.findById(ativo.getId()).orElseThrow();

        assertEquals(aprovado.getId(), recarregado.getUsuario().getId());
        assertEquals(inicio, recarregado.getHoraPermissao());
        assertEquals(1, usuarioRepository.findByStatus(UsuarioStatus.PENDENTE).size());
        assertEquals(1, usuarioRepository.findByStatusAndRole(UsuarioStatus.APROVADO, Role.USER).size());
        assertEquals(2, acessoRepository.findByUsuarioIdAndRevogadoFalse(aprovado.getId()).size());
        assertEquals(1, acessoRepository.findByRevogadoFalseAndHoraExpiracaoBefore(inicio).size());
    }

    @Test
    void bancoRejeitaEmailDuplicado() {
        salvarUsuario("duplicado@example.com", UsuarioStatus.PENDENTE);
        entityManager.flush();

        assertThrows(DataIntegrityViolationException.class, () -> usuarioRepository.save(Usuario.builder()
                .nome("Outro")
                .email("duplicado@example.com")
                .senha("senha")
                .role(Role.USER)
                .status(UsuarioStatus.PENDENTE)
                .build()));
    }

    @Test
    void bancoRejeitaEmailNulo() {
        assertThrows(DataIntegrityViolationException.class, () -> usuarioRepository.save(Usuario.builder()
                .nome("Sem e-mail")
                .senha("senha")
                .role(Role.USER)
                .status(UsuarioStatus.PENDENTE)
                .build()));
    }

    @Test
    void executaFluxoCadastroAprovacaoLoginConcessaoERevogacao() {
        Usuario cadastrado = usuarioService.registrarUsuario(
                "Usuário", "usuario@example.com", "senha-segura");
        assertEquals(UsuarioStatus.PENDENTE, cadastrado.getStatus());

        Usuario aprovado = usuarioService.aprovar(cadastrado.getId());
        AuthResponseDto login = authService.login("usuario@example.com", "senha-segura");
        Acesso acesso = acessoService.darPermissao(aprovado.getId(), "Painel", 300);
        Acesso revogado = acessoService.revogar(acesso.getId());
        entityManager.flush();

        assertEquals(UsuarioStatus.APROVADO, aprovado.getStatus());
        assertNotNull(login.token());
        assertEquals(Role.USER, login.role());
        assertEquals(aprovado.getId(), acesso.getUsuario().getId());
        assertTrue(revogado.isRevogado());
    }

    @Test
    void persisteDatasUtcEExpiraAcessoAutomaticamente() {
        Usuario usuario = salvarUsuario("datas@example.com", UsuarioStatus.APROVADO);
        Instant antes = Instant.now();
        Acesso acesso = acessoService.darPermissao(usuario.getId(), "Temporário", 2);
        Instant depois = Instant.now();

        assertFalse(acesso.getHoraPermissao().isBefore(antes));
        assertFalse(acesso.getHoraPermissao().isAfter(depois));
        assertEquals(acesso.getHoraPermissao().plusSeconds(2), acesso.getHoraExpiracao());

        acesso.setHoraExpiracao(Instant.now().minusSeconds(1));
        acessoRepository.saveAndFlush(acesso);
        entityManager.clear();
        acessoScheduler.revogarAcessosExpirados();
        entityManager.flush();
        entityManager.clear();

        assertTrue(acessoRepository.findById(acesso.getId()).orElseThrow().isRevogado());
    }

    private Usuario salvarUsuario(String email, UsuarioStatus status) {
        return usuarioRepository.save(Usuario.builder()
                .nome("Usuário")
                .email(email)
                .senha("senha")
                .role(Role.USER)
                .status(status)
                .build());
    }
}
