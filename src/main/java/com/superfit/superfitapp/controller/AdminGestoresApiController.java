package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.admin.GestorCreateDTO;
import com.superfit.superfitapp.dto.admin.GestorResponseDTO;
import com.superfit.superfitapp.dto.admin.GestorUpdateDTO;
import com.superfit.superfitapp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Map;

/**
 * Controller REST API para gerenciamento de Gestores pelo ADMIN.
 * Expõe endpoints JSON para operações CRUD de gestores.
 * Todos os endpoints verificam manualmente autenticação e role ADMIN.
 */
@RestController
@RequestMapping("/admin/api/gestores")
public class AdminGestoresApiController {

    @Autowired
    private AdminService adminService;

    /**
     * Lista todos os gestores cadastrados.
     * Valida autenticação e role ADMIN manualmente.
     * 
     * @param authentication Objeto de autenticação do Spring Security
     * @return ResponseEntity com lista de gestores ou erro 401/403
     */
    @GetMapping
    public ResponseEntity<?> listar(Authentication authentication) {
        // Verificar se está autenticado e é ADMIN
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Não autenticado"));
        }
        
        if (!authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acesso negado"));
        }
        
        return ResponseEntity.ok(adminService.listarGestores());
    }

    /**
     * Cria um novo gestor.
     * Valida autenticação e role ADMIN manualmente.
     * Recebe Map com email e password, converte para GestorCreateDTO.
     * 
     * @param request Map contendo email e password
     * @param authentication Objeto de autenticação do Spring Security
     * @return ResponseEntity com gestor criado ou erro 401/403/400
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, String> request, Authentication authentication) {
        // Verificar autenticação
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Não autenticado"));
        }
        
        if (!authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acesso negado"));
        }
        
        try {
            GestorCreateDTO dto = new GestorCreateDTO(
                request.get("email"),
                request.get("password")
            );
            GestorResponseDTO response = adminService.cadastrarGestor(dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Atualiza um gestor existente.
     * Valida autenticação e role ADMIN manualmente.
     * Permite atualizar email e/ou password.
     * 
     * @param id ID do gestor a ser atualizado
     * @param request Map contendo email e password
     * @param authentication Objeto de autenticação do Spring Security
     * @return ResponseEntity com gestor atualizado ou erro 401/403/400
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, String> request, Authentication authentication) {
        // Verificar autenticação
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Não autenticado"));
        }
        
        if (!authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acesso negado"));
        }
        
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            GestorUpdateDTO dto = new GestorUpdateDTO(email, password);
            GestorResponseDTO response = adminService.atualizarGestor(id, dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Remove um gestor do sistema.
     * Valida autenticação e role ADMIN manualmente.
     * 
     * @param id ID do gestor a ser removido
     * @param authentication Objeto de autenticação do Spring Security
     * @return ResponseEntity com status 204 ou erro 401/403/400
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id, Authentication authentication) {
        // Verificar autenticação
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Não autenticado"));
        }
        
        if (!authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acesso negado"));
        }
        
        try {
            adminService.excluirGestor(id);
            return ResponseEntity.ok(Map.of("message", "Gestor deletado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}

