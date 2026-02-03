package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.LoginRequest;
import com.superfit.superfitapp.dto.LoginResponse;
import com.superfit.superfitapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {

        String token = Objects.requireNonNull(
            authService.login(request),
            "Token não pode ser nulo"
        );

        // Criar um cookie seguro com o token JWT
        ResponseCookie cookie = ResponseCookie
                .from("jwtToken", token)
                .httpOnly(true)
                .secure(false) // Mudar para true em produção com HTTPS
                .path("/")
                .maxAge(24 * 60 * 60) // 24 horas
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new LoginResponse(token));
    }

}