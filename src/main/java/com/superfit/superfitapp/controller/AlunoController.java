package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.aluno.AlunoCreateDTO;
import com.superfit.superfitapp.dto.aluno.AlunoResponseDTO;
import com.superfit.superfitapp.dto.aluno.AlunoUpdateDTO;
import com.superfit.superfitapp.dto.treino.TreinoResponseDTO;
import com.superfit.superfitapp.service.AlunoService;
import com.superfit.superfitapp.service.TreinoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Alunos.
 * Expõe endpoints para CRUD de alunos com controle de acesso baseado em roles.
 * 
 * Regras de acesso:
 * - ADMIN/GESTOR: acesso total a todos os alunos
 * - PROFESSOR: acesso apenas aos seus próprios alunos
 * - ALUNO: acesso apenas aos próprios dados
 */
@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final TreinoService treinoService;

    public AlunoController(AlunoService alunoService, TreinoService treinoService) {
        this.alunoService = alunoService;
        this.treinoService = treinoService;
    }

    /**
     * Cria um novo aluno no sistema.
     * Acesso: ADMIN, GESTOR ou PROFESSOR.
     * 
     * Lógica:
     * - Valida e cria o aluno vinculando-o a um professor
     * - Define o aluno como ativo por padrão
     * 
     * @param dto Dados do aluno (nome, email, telefone, professorId)
     * @return ResponseEntity com status 201 e dados do aluno criado
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
     * Lista alunos de acordo com a role do usuário.
     * Acesso: ADMIN, GESTOR ou PROFESSOR.
     * 
     * Lógica:
     * - ADMIN/GESTOR: retorna todos os alunos
     * - PROFESSOR: retorna apenas seus alunos (filtro aplicado no service)
     * 
     * @return ResponseEntity com lista de alunos
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','PROFESSOR')")
    public ResponseEntity<List<AlunoResponseDTO>> listar() {
        return ResponseEntity.ok(alunoService.listar());
    }

    /**
     * Busca um aluno específico por ID.
     * Acesso com validações de permissão:
     * - ADMIN/GESTOR: podem buscar qualquer aluno
     * - PROFESSOR: pode buscar apenas seus alunos (valida via @alunoService.isAlunoDoProfessor)
     * - ALUNO: pode buscar apenas seus próprios dados (valida via @alunoService.isAlunoDoToken)
     * 
     * @param id ID do aluno
     * @return ResponseEntity com dados do aluno
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
     * Atualiza os dados de um aluno existente.
     * Acesso com validações de permissão:
     * - ADMIN/GESTOR: podem atualizar qualquer aluno
     * - PROFESSOR: pode atualizar apenas seus alunos
     * - ALUNO: pode atualizar apenas seus próprios dados
     * 
     * @param id ID do aluno a ser atualizado
     * @param dto Novos dados (nome, telefone, ativo)
     * @return ResponseEntity com dados atualizados
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
     * Remove um aluno do sistema.
     * Acesso restrito: apenas ADMIN ou GESTOR.
     * 
     * @param id ID do aluno a ser removido
     * @return ResponseEntity com status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        alunoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos os treinos do aluno autenticado.
     * Acesso restrito: apenas ALUNO.
     * 
     * @return ResponseEntity com lista de treinos do aluno
     */
    @GetMapping("/treinos")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<TreinoResponseDTO>> listarTreinos() {
        return ResponseEntity.ok(treinoService.listarTodos());
    }
}
