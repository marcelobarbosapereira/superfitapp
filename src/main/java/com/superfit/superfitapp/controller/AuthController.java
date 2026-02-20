package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.LoginRequest;
import com.superfit.superfitapp.dto.LoginResponse;
import com.superfit.superfitapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

/**
 * Controller REST para autenticação de usuários.
 * Expõe endpoint de login que gera token JWT e define cookie seguro.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    /**
     * Endpoint de autenticação de usuários.
     * 
     * Lógica:
     * 1. Autentica usuário via AuthService (valida credenciais e gera JWT)
     * 2. Cria um cookie HttpOnly com o token para segurança adicional
     * 3. Adiciona o cookie ao response header
     * 4. Retorna o token no corpo da resposta
     * 
     * Configurações do cookie:
     * - httpOnly: true (não acessível via JavaScript)
     * - secure: configurável via application.properties (true em produção HTTPS)
     * - maxAge: 24 horas
     * - sameSite: Lax (proteção contra CSRF)
     * 
     * @param request DTO com email e password
     * @param response HttpServletResponse para adicionar cookie
     * @return ResponseEntity com token JWT no corpo
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {

        String token = Objects.requireNonNull(
            authService.login(request),
            "Token não pode ser nulo"
        );

        System.out.println("🔐 [Auth] Login bem-sucedido | Cookie Secure: " + cookieSecure);

        // Criar um cookie seguro com o token JWT
        ResponseCookie cookie = ResponseCookie
                .from("jwtToken", token)
                .httpOnly(true)
                .secure(cookieSecure) // Configurável: true em produção HTTPS
                .path("/")
                .maxAge(24 * 60 * 60) // 24 horas
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new LoginResponse(token));
    }

}