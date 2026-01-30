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
    }
}
