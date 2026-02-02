package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AdminService adminService;

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
