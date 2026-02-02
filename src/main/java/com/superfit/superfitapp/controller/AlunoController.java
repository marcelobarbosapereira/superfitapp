package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.aluno.AlunoCreateDTO;
import com.superfit.superfitapp.dto.aluno.AlunoResponseDTO;
import com.superfit.superfitapp.dto.aluno.AlunoUpdateDTO;
import com.superfit.superfitapp.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    /**
     * Criar aluno
     * Acesso: ADMIN / GESTOR / PROFESSOR
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','PROFESSOR')")
    public ResponseEntity<AlunoResponseDTO> criar(
            @RequestBody AlunoCreateDTO dto
    ) {
        AlunoResponseDTO response = alunoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar alunos
     * ADMIN / GESTOR → todos
     * PROFESSOR → apenas seus alunos
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','PROFESSOR')")
    public ResponseEntity<List<AlunoResponseDTO>> listar() {
        return ResponseEntity.ok(alunoService.listar());
    }

    /**
     * Buscar aluno por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("""
        hasAnyRole('ADMIN','GESTOR')
        or (hasRole('PROFESSOR') and @alunoService.isAlunoDoProfessor(#id))
        or (hasRole('ALUNO') and @alunoService.isAlunoDoToken(#id))
    """)
    public ResponseEntity<AlunoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.buscarPorId(id));
    }

    /**
     * Atualizar aluno
     */
    @PutMapping("/{id}")
    @PreAuthorize("""
        hasAnyRole('ADMIN','GESTOR')
        or (hasRole('PROFESSOR') and @alunoService.isAlunoDoProfessor(#id))
        or (hasRole('ALUNO') and @alunoService.isAlunoDoToken(#id))
    """)
    public ResponseEntity<AlunoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody AlunoUpdateDTO dto
    ) {
        return ResponseEntity.ok(alunoService.atualizar(id, dto));
    }

    /**
     * Remover aluno
     * ADMIN / GESTOR
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        alunoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
