package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

/**
 * Controller para renderização das páginas HTML do painel administrativo.
 * Gerencia as rotas de visualização do dashboard do ADMIN com Thymeleaf.
 * 
 * Validação de acesso:
 * - Verifica autenticação e role ADMIN em cada método
 * - Redireciona para /home se não autorizado
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AdminService adminService;

    /**
     * Página de boas-vindas do admin.
     * Verifica autenticação e role ADMIN antes de renderizar.
     * 
     * @param model Model do Thymeleaf
     * @param authentication Objeto de autenticação do Spring Security
     * @return Nome da view (admin-welcome) ou redirect
     */
    @GetMapping("")
    public String welcome(Model model, Authentication authentication) {
        // Se não autenticado, redirecionar para login
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        
        // Se não tem role ADMIN, redirecionar para home
        if (!authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/home";
        }
        
        model.addAttribute("userName", authentication.getName());
        return "admin-welcome";
    }

    /**
     * Dashboard principal do admin com lista de gestores.
     * Verifica autenticação e role ADMIN antes de renderizar.
     * Adiciona lista de gestores ao model para exibição na view.
     * 
     * @param model Model do Thymeleaf
     * @param authentication Objeto de autenticação do Spring Security
     * @return Nome da view (admin-dashboard) ou redirect
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Se não autenticado, redirecionar para login
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        
        // Se não tem role ADMIN, redirecionar para home
        if (!authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/home";
        }
        
        model.addAttribute("gestores", adminService.listarGestores());
        model.addAttribute("userName", authentication.getName());
        return "admin-dashboard";
    }
}
