package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.admin.ChangePasswordDTO;
import com.superfit.superfitapp.service.GestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller REST API para gerenciamento do perfil do GESTOR.
 * Expõe endpoint para alteração de senha do gestor autenticado.
 */
@RestController
@RequestMapping("/gestor/api")
public class GestorProfileApiController {

    @Autowired
    private GestorService gestorService;

    /**
     * Altera a senha do gestor autenticado.
     * Valida autenticação e role GESTOR manualmente.
     *
     * @param request Map contendo senhaAtual, novaSenha e confirmarSenha
     * @param authentication Objeto de autenticação do Spring Security
     * @return ResponseEntity com mensagem de sucesso ou erro
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Não autenticado"));
        }

        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_GESTOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acesso negado"));
        }

        try {
            String senhaAtual = request.get("senhaAtual");
            String novaSenha = request.get("novaSenha");
            String confirmarSenha = request.get("confirmarSenha");

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

            ChangePasswordDTO dto = new ChangePasswordDTO(senhaAtual, novaSenha);
            gestorService.alterarSenha(authentication.getName(), dto);

            return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
