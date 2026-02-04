package com.superfit.superfitapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.superfit.superfitapp.dto.admin.GestorCreateDTO;
import com.superfit.superfitapp.dto.admin.GestorResponseDTO;
import com.superfit.superfitapp.dto.admin.ProfessorCreateDTO;
import com.superfit.superfitapp.dto.admin.ProfessorResponseDTO;
import com.superfit.superfitapp.dto.admin.ProfessorUpdateDTO;
import com.superfit.superfitapp.dto.admin.AlunoCreateDTO;
import com.superfit.superfitapp.dto.admin.AlunoResponseDTO;
import com.superfit.superfitapp.dto.admin.AlunoUpdateDTO;
import com.superfit.superfitapp.service.AdminService;
import java.util.List;

/**
 * Controller REST para gerenciamento administrativo do sistema.
 * Expõe endpoints para CRUD de Gestores, Professores e Alunos.
 * Todos os endpoints requerem autenticacão com role ADMIN (validação geralmente configurada na SecurityConfig).
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ===============================
    // GESTORES
    // ===============================

    /**
     * Cria um novo gestor no sistema.
     * Registra um usuário com role GESTOR.
     * 
     * @param dto Dados do gestor (email, password)
     * @return ResponseEntity com dados do gestor criado
     */
    @PostMapping("/gestores")
    public ResponseEntity<GestorResponseDTO> criarGestor(
            @Valid @RequestBody GestorCreateDTO dto
    ) {
        GestorResponseDTO response = adminService.cadastrarGestor(dto);
        return ResponseEntity.ok(response);
    }

    // ===============================
    // PROFESSORES
    // ===============================

    /**
     * Cria um novo professor no sistema.
     * Registra um usuário com role PROFESSOR e cria a entidade Professor vinculada.
     * 
     * @param dto Dados do professor (nome, email, telefone, CREFI, password)
     * @return ResponseEntity com dados do professor criado
     */
    @PostMapping("/professores")
    public ResponseEntity<ProfessorResponseDTO> criarProfessor(
            @Valid @RequestBody ProfessorCreateDTO dto
    ) {
        ProfessorResponseDTO response = adminService.cadastrarProfessor(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os professores cadastrados.
     * 
     * @return ResponseEntity com lista de professores
     */
    @GetMapping("/professores")
    public ResponseEntity<List<ProfessorResponseDTO>> listarProfessores() {
        List<ProfessorResponseDTO> professores = adminService.listarProfessores();
        return ResponseEntity.ok(professores);
    }

    /**
     * Atualiza os dados de um professor existente.
     * 
     * @param id ID do professor
     * @param dto Novos dados (nome, telefone, CREFI)
     * @return ResponseEntity com dados atualizados
     */
    @PutMapping("/professores/{id}")
    public ResponseEntity<ProfessorResponseDTO> atualizarProfessor(
            @PathVariable Long id,
            @Valid @RequestBody ProfessorUpdateDTO dto
    ) {
        ProfessorResponseDTO response = adminService.atualizarProfessor(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove um professor do sistema.
     * 
     * @param id ID do professor a ser removido
     * @return ResponseEntity com status 204
     */
    @DeleteMapping("/professores/{id}")
    public ResponseEntity<Void> deletarProfessor(@PathVariable Long id) {
        adminService.excluirProfessor(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // ALUNOS
    // ===============================

    /**
     * Cria um novo aluno no sistema.
     * Registra um usuário com role ALUNO e cria a entidade Aluno vinculada a um professor.
     * 
     * @param dto Dados do aluno (nome, email, telefone, professorId, password)
     * @return ResponseEntity com dados do aluno criado
     */
    @PostMapping("/alunos")
    public ResponseEntity<AlunoResponseDTO> criarAluno(
            @Valid @RequestBody AlunoCreateDTO dto
    ) {
        AlunoResponseDTO response = adminService.cadastrarAluno(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os alunos cadastrados.
     * 
     * @return ResponseEntity com lista de alunos
     */
    @GetMapping("/alunos")
    public ResponseEntity<List<AlunoResponseDTO>> listarAlunos() {
        List<AlunoResponseDTO> alunos = adminService.listarAlunos();
        return ResponseEntity.ok(alunos);
    }

    /**
     * Atualiza os dados de um aluno existente.
     * 
     * @param id ID do aluno
     * @param dto Novos dados (nome, telefone, ativo)
     * @return ResponseEntity com dados atualizados
     */
    @PutMapping("/alunos/{id}")
    public ResponseEntity<AlunoResponseDTO> atualizarAluno(
            @PathVariable Long id,
            @Valid @RequestBody AlunoUpdateDTO dto
    ) {
        AlunoResponseDTO response = adminService.atualizarAluno(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove um aluno do sistema.
     * 
     * @param id ID do aluno a ser removido
     * @return ResponseEntity com status 204
     */
    @DeleteMapping("/alunos/{id}")
    public ResponseEntity<Void> deletarAluno(@PathVariable Long id) {
        adminService.excluirAluno(id);
        return ResponseEntity.noContent().build();
    }

}
