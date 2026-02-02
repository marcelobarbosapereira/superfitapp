package com.superfitapp.controller;

import com.superfitapp.dto.professor.ProfessorCreateDTO;
import com.superfitapp.dto.professor.ProfessorResponseDTO;
import com.superfitapp.dto.professor.ProfessorUpdateDTO;
import com.superfitapp.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorServiceImp professorService;

    /**
     * Criar professor
     * Acesso: ADMIN / GESTOR
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
     * Listar todos os professores
     * Acesso: ADMIN / GESTOR
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<List<ProfessorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(professorService.listarTodos());
    }

    /**
     * Buscar professor por ID
     * Acesso:
     * - ADMIN / GESTOR → qualquer professor
     * - PROFESSOR → apenas o próprio
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
     * Atualizar professor
     * Acesso:
     * - ADMIN / GESTOR → qualquer professor
     * - PROFESSOR → apenas o próprio
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
     * Remover professor
     * Acesso: ADMIN / GESTOR
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        professorService.remover(id);
        return ResponseEntity.noContent().build();
    }
}

