package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.LoginRequest;
import com.superfit.superfitapp.model.Role;
import com.superfit.superfitapp.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public String login(LoginRequest request) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );

    String email = authentication.getName();

    String roleStr = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Usuário sem role"));

    Role role = Role.valueOf(roleStr);
    
    // Log de login no terminal
    System.out.println("✅ LOGIN REALIZADO");
    System.out.println("   Usuário: " + email);
    System.out.println("   Role: " + role);
    System.out.println("   Hora: " + java.time.LocalDateTime.now());
    System.out.println("-----------------------------------");
    
    return jwtService.generateToken(email, role);
}
}


