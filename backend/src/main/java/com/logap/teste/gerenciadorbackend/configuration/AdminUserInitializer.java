package com.logap.teste.gerenciadorbackend.configuration;

import com.logap.teste.gerenciadorbackend.model.Usuario;
import com.logap.teste.gerenciadorbackend.model.enums.Perfil;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
@ConditionalOnProperty(name = "admin.creation.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class AdminUserInitializer {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.name}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            if (adminEmail == null || adminEmail.isEmpty() || adminPassword == null || adminPassword.isEmpty()) {
                System.err.println("AVISO: A criação do usuário admin está habilitada, mas o email ou a senha não foram configurados. Pulando a criação.");
                return;
            }

            if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
                System.out.println("Criando usuário administrador com dados de ambiente...");
                Usuario admin = new Usuario();
                admin.setNome(adminUsername);
                admin.setEmail(adminEmail);
                admin.setSenha(passwordEncoder.encode(adminPassword));
                admin.setPerfil(Perfil.ADMINISTRADOR);
                admin.setDataCriacao(Instant.now());
                usuarioRepository.save(admin);
                System.out.println("Usuário administrador criado com sucesso!");
            }
        };
    }

}
