package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.service.MensalidadeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/aluno")
@PreAuthorize("hasRole('ALUNO')")
public class AlunoPortalController {

    private final MensalidadeService mensalidadeService;

    public AlunoPortalController(MensalidadeService mensalidadeService) {
        this.mensalidadeService = mensalidadeService;
    }

    /**
     * Dashboard do aluno - página principal
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Aqui você pode adicionar informações gerais do aluno
        return "aluno-dashboard";
    }

    /**
     * Visualizar mensalidades do aluno
     */
    @GetMapping("/mensalidades")
    public String visualizarMensalidades(Model model) {
        // As mensalidades serão carregadas via JavaScript/AJAX
        return "aluno-mensalidades";
    }

    /**
     * Página de pagamento (fictícia)
     */
    @GetMapping("/pagamento/{mensalidadeId}")
    public String telapagamento(@PathVariable Long mensalidadeId, Model model) {
        model.addAttribute("mensalidadeId", mensalidadeId);
        model.addAttribute("valor", 150.00); // Valor fictício - seria obtido do banco
        return "aluno-pagamento";
    }

    /**
     * Visualizar medidas e evolução do aluno
     */
    @GetMapping("/medidas")
    public String visualizarMedidas(Model model) {
        return "aluno-medidas";
    }

    /**
     * Visualizar treinos do aluno
     */
    @GetMapping("/treinos")
    public String visualizarTreinos(Model model) {
        return "aluno-treinos";
    }

    /**
     * Página de perfil do aluno
     */
    @GetMapping("/perfil")
    public String perfil(Model model) {
        return "aluno-perfil";
    }
}
