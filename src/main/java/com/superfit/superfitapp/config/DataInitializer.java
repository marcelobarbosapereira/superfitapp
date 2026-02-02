package com.superfit.superfitapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.superfit.superfitapp.model.Role;
import com.superfit.superfitapp.model.User;
import com.superfit.superfitapp.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("Iniciando DataInitializer...");
        System.out.println("========================================");
        
        // Cria usuário ADMIN
        criarAdminSeNaoExistir();
        
        // Cria GESTOR padrão
        criarGestorSeNaoExistir();
        
        System.out.println("========================================");
        System.out.println("DataInitializer finalizado!");
        System.out.println("========================================\n");
    }
    
    private void criarAdminSeNaoExistir() {
        if (!userRepository.existsByEmail("admin")) {
            User adminUser = new User(
                "admin",
                passwordEncoder.encode("12345"),
                Role.ROLE_ADMIN
            );
            userRepository.save(adminUser);
            System.out.println("✅ Usuário ADMIN criado com sucesso!");
            System.out.println("   Email: admin | Senha: 12345");
        } else {
            System.out.println("ℹ️ Usuário ADMIN já existe");
        }
    }
    
    private void criarGestorSeNaoExistir() {
        if (!userRepository.existsByEmail("gestor")) {
            User gestorUser = new User(
                "gestor",
                passwordEncoder.encode("12345"),
                Role.ROLE_GESTOR
            );
            userRepository.save(gestorUser);
            System.out.println("✅ Usuário GESTOR criado com sucesso!");
            System.out.println("   Email: gestor | Senha: 12345");
        } else {
            System.out.println("ℹ️ Usuário GESTOR já existe");
        }
    }
}
