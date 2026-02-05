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
}
