package com.superfit.superfitapp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller para renderização das páginas HTML do painel do PROFESSOR.
 */
@Controller
@RequestMapping("/professor")
@PreAuthorize("hasRole('PROFESSOR')")
public class ProfessorDashboardController {

    /**
     * Dashboard principal do professor.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "professor-dashboard";
    }

    /**
     * Página de gerenciamento de treinos do professor.
     */
    @GetMapping("/treinos")
    public String treinos(Model model) {
        return "professor-treinos";
    }

    /**
     * Página de registro de medidas do professor.
     */
    @GetMapping("/medidas")
    public String medidas(Model model) {
        return "professor-medidas";
    }

    /**
     * Página de relatórios de progresso do professor.
     */
    @GetMapping("/relatorios")
    public String relatorios(Model model) {
        return "professor-relatorios";
    }
}
