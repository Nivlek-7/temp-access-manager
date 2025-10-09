package com.example.demo.task;

import com.example.demo.model.Acesso;
import com.example.demo.repository.AcessoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcessoScheduler {

    private final AcessoRepository acessoRepository;

    @Scheduled(fixedRate = 60000) // a cada 60secs executa
    public void revogarAcessosExpirados() {
        LocalDateTime agora = LocalDateTime.now();
        List<Acesso> expirados = acessoRepository.findByRevogadoFalseAndHoraExpiracaoBefore(agora);

        if (!expirados.isEmpty()) {
            expirados.forEach(a -> a.setRevogado(true));
            acessoRepository.saveAll(expirados);
            System.out.println(expirados.size() + " acessos revogados automaticamente.");
        }
    }
}



