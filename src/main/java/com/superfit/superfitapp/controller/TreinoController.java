package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.treino.TreinoCreateDTO;
import com.superfit.superfitapp.dto.treino.TreinoResponseDTO;
import com.superfit.superfitapp.dto.treino.TreinoUpdateDTO;
import com.superfit.superfitapp.service.TreinoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    /**
     * Criar treino
     * Acesso: PROFESSOR
     */
    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<TreinoResponseDTO> criar(
            @RequestBody TreinoCreateDTO dto
    ) {
        TreinoResponseDTO response = treinoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar treinos
     * PROFESSOR → seus treinos
     * ALUNO → seus treinos
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<TreinoResponseDTO>> listar() {
        return ResponseEntity.ok(treinoService.listarTodos());
    }

    /**
     * Buscar treino por ID
     * PROFESSOR → apenas seus treinos
     * ALUNO → apenas seus treinos
     */
    @GetMapping("/{id}")
    @PreAuthorize("""
        (hasRole('PROFESSOR') and @treinoService.isTreinoDoProfessor(#id))
        or (hasRole('ALUNO') and @treinoService.isTreinoDoAluno(#id))
    """)
    public ResponseEntity<TreinoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.buscarPorId(id));
    }

    /**
     * Atualizar treino
     * Acesso: PROFESSOR (apenas seus treinos)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR') and @treinoService.isTreinoDoProfessor(#id)")
    public ResponseEntity<TreinoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody TreinoUpdateDTO dto
    ) {
        return ResponseEntity.ok(treinoService.atualizar(id, dto));
    }

    /**
     * Remover treino
     * Acesso: PROFESSOR (apenas seus treinos)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR') and @treinoService.isTreinoDoProfessor(#id)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        treinoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
