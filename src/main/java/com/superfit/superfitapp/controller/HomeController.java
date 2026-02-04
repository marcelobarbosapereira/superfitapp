package com.superfit.superfitapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller para renderização da página inicial do sistema.
 * Gerencia as rotas públicas de boas-vindas.
 */
@Controller
public class HomeController {

    /**
     * Redireciona a raiz do site para /home.
     * 
     * @return Redirect para /home
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    /**
     * Renderiza a página inicial (home.html) do sistema.
     * Página pública acessível sem autenticação.
     * 
     * @return Nome da view (home)
     */
    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
