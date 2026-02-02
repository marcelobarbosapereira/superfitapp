package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.admin.ChangePasswordDTO;
import com.superfit.superfitapp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Map;

@RestController
@RequestMapping("/admin/api")
public class AdminProfileApiController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, Authentication authentication) {
        // Verificar se está autenticado e é ADMIN
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Não autenticado"));
        }
        
        if (!authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acesso negado"));
        }
        
        try {
            String senhaAtual = request.get("senhaAtual");
            String novaSenha = request.get("novaSenha");
            String confirmarSenha = request.get("confirmarSenha");

            // Validar campos
            if (senhaAtual == null || senhaAtual.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Senha atual é obrigatória"));
            }

            if (novaSenha == null || novaSenha.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nova senha é obrigatória"));
            }

            if (novaSenha.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nova senha deve ter no mínimo 6 caracteres"));
            }

            if (!novaSenha.equals(confirmarSenha)) {
                return ResponseEntity.badRequest().body(Map.of("error", "As senhas não conferem"));
            }

            // Criar DTO e alterar senha
            ChangePasswordDTO dto = new ChangePasswordDTO(senhaAtual, novaSenha);
            adminService.alterarSenha(authentication.getName(), dto);

            return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
