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

/**
 * Controller REST para gerenciamento de Treinos e Exercícios.
 * Expõe endpoints para CRUD de treinos com seus exercícios associados.
 * 
 * Regras de acesso:
 * - PROFESSOR: cria e gerencia treinos para seus alunos
 * - ALUNO: visualiza apenas seus próprios treinos
 */
@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    /**
     * Cria um novo treino com lista de exercícios.
     * Acesso restrito: apenas PROFESSOR.
     * O professor autenticado é automaticamente vinculado ao treino.
     * 
     * @param dto Dados do treino (nome, alunoId, lista de exercícios)
     * @return ResponseEntity com status 201 e dados do treino criado
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
     * Lista treinos de acordo com a role do usuário.
     * Acesso: PROFESSOR ou ALUNO.
     * 
     * Lógica:
     * - PROFESSOR: retorna treinos que ele criou
     * - ALUNO: retorna treinos atribuídos a ele
     * 
     * @return ResponseEntity com lista de treinos
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<TreinoResponseDTO>> listar() {
        return ResponseEntity.ok(treinoService.listarTodos());
    }

    /**
     * Busca um treino específico por ID com seus exercícios.
     * Acesso:
     * - PROFESSOR: pode buscar apenas treinos que criou (valida via @treinoService.isTreinoDoProfessor)
     * - ALUNO: pode buscar apenas treinos atribuídos a ele (valida via @treinoService.isTreinoDoAluno)
     * 
     * @param id ID do treino
     * @return ResponseEntity com dados do treino e seus exercícios
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
     * Atualiza um treino existente e seus exercícios.
     * Acesso restrito: apenas PROFESSOR que criou o treino.
     * Remove todos os exercícios antigos e adiciona os novos da lista.
     * 
     * @param id ID do treino a ser atualizado
     * @param dto Novos dados (nome, lista de exercícios)
     * @return ResponseEntity com dados atualizados
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
     * Remove um treino do sistema.
     * Acesso restrito: apenas PROFESSOR que criou o treino.
     * Remove também todos os exercícios associados (cascade).
     * 
     * @param id ID do treino a ser removido
     * @return ResponseEntity com status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR') and @treinoService.isTreinoDoProfessor(#id)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        treinoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
