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

/**
 * Serviço de autenticação de usuários.
 * Gerencia o processo de login utilizando Spring Security e geração de tokens JWT.
 */
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    /**
     * Autentica um usuário e gera um token JWT.
     * 
     * Lógica:
     * 1. Autentica o usuário usando AuthenticationManager com email e senha
     * 2. Extrai o email e a primeira role (authority) da autenticação
     * 3. Converte a role para o enum Role
     * 4. Registra o login no console (email, role, hora)
     * 5. Gera e retorna um token JWT usando JwtService
     * 
     * @param request DTO contendo email e password
     * @return Token JWT válido para autenticação
     * @throws RuntimeException se o usuário não possuir role definida
     */
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


