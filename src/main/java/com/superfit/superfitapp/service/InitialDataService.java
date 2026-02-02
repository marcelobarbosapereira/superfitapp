package com.superfit.superfitapp.service;

import com.superfit.superfitapp.model.Role;
import com.superfit.superfitapp.model.User;
import com.superfit.superfitapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class InitialDataService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (userRepository.findByEmail("admin@superfit.com").isEmpty()) {
            User adminUser = new User(
                    "admin@superfit.com",
                    passwordEncoder.encode("123456"),
                    Role.ROLE_ADMIN
            );
            userRepository.save(adminUser);
            System.out.println("✅ Admin user created: admin@superfit.com / 123456");
        } else {
            System.out.println("ℹ️ Admin user already exists");
        }
        // Create default João user with role Aluno
        if (userRepository.findByEmail("joao@superfit.com").isEmpty()) {
            User joao = new User(
                    "joao@superfit.com",
                    passwordEncoder.encode("123456"),
                    Role.ROLE_ALUNO
            );
            userRepository.save(joao);
            System.out.println("✅ User created: joao@superfit.com / 123456 (ROLE_ALUNO)");
        } else {
            System.out.println("ℹ️ User joao@superfit.com already exists");
        }
        // Create default Maria user with role Professor
        if (userRepository.findByEmail("maria@superfit.com").isEmpty()) {
            User maria = new User(
                    "maria@superfit.com",
                    passwordEncoder.encode("123456"),
                    Role.ROLE_PROFESSOR
            );
            userRepository.save(maria);
            System.out.println("✅ User created: maria@superfit.com / 123456 (ROLE_PROFESSOR)");
        } else {
            System.out.println("ℹ️ User maria@superfit.com already exists");
        }
    }
}
