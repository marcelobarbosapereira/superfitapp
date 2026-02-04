package com.superfit.superfitapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller para gerenciamento de logout de usuários.
 * Limpa o cookie JWT e redireciona para a página inicial.
 */
@Controller
public class LogoutController {
    
    /**
     * Realiza o logout do usuário.
     * 
     * Lógica:
     * - Limpa o cookie JWT definindo valor vazio e maxAge=0
     * - Mantém as mesmas configurações do cookie (httpOnly, path, sameSite)
     * - Redireciona para a página inicial
     * 
     * @param response HttpServletResponse para adicionar cookie de limpeza
     * @return Redirect para /home
     */
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Limpar o cookie do JWT
        ResponseCookie cookie = ResponseCookie
                .from("jwtToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        return "redirect:/home";
    }
}
