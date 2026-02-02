package com.superfit.superfitapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {
    
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
