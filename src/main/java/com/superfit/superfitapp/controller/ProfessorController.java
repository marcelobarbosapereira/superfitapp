package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.professor.ProfessorCreateDTO;
import com.superfit.superfitapp.dto.professor.ProfessorResponseDTO;
import com.superfit.superfitapp.dto.professor.ProfessorUpdateDTO;
import com.superfit.superfitapp.service.ProfessorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Professores.
 * Expõe endpoints para CRUD de professores com controle de acesso baseado em roles.
 * 
 * Regras de acesso:
 * - ADMIN/GESTOR: acesso total a todos os professores
 * - PROFESSOR: acesso apenas aos próprios dados
 */
@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    /**
     * Cria um novo professor no sistema.
     * Acesso restrito: apenas ADMIN ou GESTOR.
     * 
     * @param dto Dados do professor (nome, email, telefone, CREFI)
     * @return ResponseEntity com status 201 e dados do professor criado
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<ProfessorResponseDTO> criar(
            @RequestBody ProfessorCreateDTO dto
    ) {
        ProfessorResponseDTO response = professorService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todos os professores cadastrados.
     * Acesso restrito: apenas ADMIN ou GESTOR.
     * 
     * @return ResponseEntity com lista de todos os professores
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<List<ProfessorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(professorService.listarTodos());
    }

    /**
     * Busca um professor específico por ID.
     * Acesso:
     * - ADMIN/GESTOR: podem buscar qualquer professor
     * - PROFESSOR: pode buscar apenas seus próprios dados (valida via @professorService.isProfessorDoToken)
     * 
     * @param id ID do professor
     * @return ResponseEntity com dados do professor
     */
    @GetMapping("/{id}")
    @PreAuthorize("""
        hasAnyRole('ADMIN','GESTOR')
        or (hasRole('PROFESSOR') and @professorService.isProfessorDoToken(#id))
    """)
    public ResponseEntity<ProfessorResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(professorService.buscarPorId(id));
    }

    /**
     * Atualiza os dados de um professor existente.
     * Acesso:
     * - ADMIN/GESTOR: podem atualizar qualquer professor
     * - PROFESSOR: pode atualizar apenas seus próprios dados
     * 
     * @param id ID do professor a ser atualizado
     * @param dto Novos dados (nome, telefone, CREFI)
     * @return ResponseEntity com dados atualizados
     */
    @PutMapping("/{id}")
    @PreAuthorize("""
        hasAnyRole('ADMIN','GESTOR')
        or (hasRole('PROFESSOR') and @professorService.isProfessorDoToken(#id))
    """)
    public ResponseEntity<ProfessorResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ProfessorUpdateDTO dto
    ) {
        return ResponseEntity.ok(professorService.atualizar(id, dto));
    }

    /**
     * Remove um professor do sistema.
     * Acesso restrito: apenas ADMIN ou GESTOR.
     * 
     * @param id ID do professor a ser removido
     * @return ResponseEntity com status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        professorService.remover(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos os alunos vinculados ao professor autenticado.
     * Acesso restrito: apenas PROFESSOR.
     * 
     * @return ResponseEntity com lista de alunos do professor
     */
    @GetMapping("/alunos")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<com.superfit.superfitapp.dto.aluno.AlunoResponseDTO>> listarMeusAlunos() {
        return ResponseEntity.ok(professorService.listarMeusAlunos());
    }
}

