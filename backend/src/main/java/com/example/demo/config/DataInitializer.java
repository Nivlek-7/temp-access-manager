package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioStatus;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public DataInitializer(UsuarioRepository userRepository) {
        this.usuarioRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // verifica se usuário admin já existe, para que na primeira execução sempre tenha disponivel o ADMIN
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@admin");
            admin.setNome("admin");
            admin.setRole(Role.ADMIN);
            admin.setSenha("$2a$10$N0n5TpjUEYA.bL7slOr25eypj0h1zhtrmTo0chTYZ05/qntdAPQrq");
            admin.setStatus(UsuarioStatus.APROVADO);
            usuarioRepository.save(admin);
            System.out.println("Usuário admin criado!");
        }
    }
}
