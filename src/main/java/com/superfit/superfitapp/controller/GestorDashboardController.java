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

    @GetMapping("/alunos")
    public String alunos() {
        return "gestor-alunos";
    }

    @GetMapping("/professores")
    public String professores() {
        return "gestor-professores";
    }

    @GetMapping("/mensalidades")
    public String mensalidades() {
        return "gestor-mensalidades";
    }

    @GetMapping("/despesas")
    public String despesas() {
        return "gestor-despesas";
    }

    @GetMapping("/relatorios")
    public String relatorios() {
        return "gestor-relatorios";
    }
}
