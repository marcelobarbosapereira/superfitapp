package com.superfit.superfitapp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller para renderização das páginas HTML do painel do GESTOR.
 */
@Controller
@RequestMapping("/gestor")
@PreAuthorize("hasRole('GESTOR')")
public class GestorDashboardController {

    /**
     * Dashboard principal do gestor.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "gestor-dashboard";
    }
}
