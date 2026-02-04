package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.medidas.HistoricoEvolucaoDTO;
import com.superfit.superfitapp.dto.medidas.MedidasCreateDTO;
import com.superfit.superfitapp.dto.medidas.MedidasResponseDTO;
import com.superfit.superfitapp.dto.medidas.MedidasUpdateDTO;
import com.superfit.superfitapp.service.MedidasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Medidas corporais dos alunos.
 * Expõe endpoints para CRUD de medidas, consultas por aluno e histórico de evolução.
 * 
 * Regras de acesso:
 * - PROFESSOR: cria e gerencia medidas de seus alunos
 * - ALUNO: visualiza apenas suas próprias medidas
 */
@RestController
@RequestMapping("/api/medidas")
public class MedidasController {

    private final MedidasService medidasService;

    public MedidasController(MedidasService medidasService) {
        this.medidasService = medidasService;
    }

    /**
     * Cria uma nova medição para um aluno.
     * Acesso restrito: apenas PROFESSOR.
     * Calcula automaticamente o IMC baseado no peso e altura do aluno.
     * 
     * @param dto Dados da medição (data, peso, peito, cintura, quadril, alunoId)
     * @return ResponseEntity com status 201 e dados da medida criada incluindo IMC
     */
    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<MedidasResponseDTO> criar(
            @RequestBody MedidasCreateDTO dto
    ) {
        MedidasResponseDTO response = medidasService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista medidas de acordo com a role do usuário.
     * Acesso: PROFESSOR ou ALUNO.
     * 
     * Lógica:
     * - PROFESSOR: retorna medidas de todos os seus alunos
     * - ALUNO: retorna apenas suas próprias medidas
     * 
     * @return ResponseEntity com lista de medidas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<MedidasResponseDTO>> listar() {
        return ResponseEntity.ok(medidasService.listarTodas());
    }

    /**
     * Lista todas as medidas de um aluno específico.
     * Acesso: PROFESSOR ou ALUNO.
     * Ordenadas em ordem decrescente de data (mais recente primeiro).
     * 
     * Nota: Validação de permissão deve ser feita no service layer.
     * 
     * @param alunoId ID do aluno
     * @return ResponseEntity com lista de medidas do aluno
     */
    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<MedidasResponseDTO>> listarPorAluno(
            @PathVariable Long alunoId
    ) {
        return ResponseEntity.ok(medidasService.listarPorAluno(alunoId));
    }

    /**
     * Obtém o histórico completo de evolução física de um aluno.
     * Acesso: PROFESSOR ou ALUNO.
     * 
     * Lógica:
     * - Retorna todas as medidas em ordem cronológica
     * - Calcula evolução comparando primeira e última medição (peso e IMC)
     * 
     * @param alunoId ID do aluno
     * @return ResponseEntity com histórico de medidas e resumo de evolução
     */
    @GetMapping("/historico/{alunoId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public ResponseEntity<HistoricoEvolucaoDTO> obterHistorico(
            @PathVariable Long alunoId
    ) {
        return ResponseEntity.ok(medidasService.obterHistoricoEvolucao(alunoId));
    }

    /**
     * Busca uma medida específica por ID.
     * Acesso:
     * - PROFESSOR: pode buscar apenas medidas de seus alunos (valida via @medidasService.isMedidaDoProfessor)
     * - ALUNO: pode buscar apenas suas próprias medidas (valida via @medidasService.isMedidaDoAluno)
     * 
     * @param id ID da medida
     * @return ResponseEntity com dados da medida incluindo IMC
     */
    @GetMapping("/{id}")
    @PreAuthorize("""
        (hasRole('PROFESSOR') and @medidasService.isMedidaDoProfessor(#id))
        or (hasRole('ALUNO') and @medidasService.isMedidaDoAluno(#id))
    """)
    public ResponseEntity<MedidasResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medidasService.buscarPorId(id));
    }

    /**
     * Atualiza uma medição existente.
     * Acesso restrito: apenas PROFESSOR que supervisiona o aluno.
     * Recalcula automaticamente o IMC após atualização.
     * 
     * @param id ID da medida a ser atualizada
     * @param dto Novos dados (data, peso, peito, cintura, quadril)
     * @return ResponseEntity com dados atualizados incluindo IMC recalculado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR') and @medidasService.isMedidaDoProfessor(#id)")
    public ResponseEntity<MedidasResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody MedidasUpdateDTO dto
    ) {
        return ResponseEntity.ok(medidasService.atualizar(id, dto));
    }

    /**
     * Remove uma medição do sistema.
     * Acesso restrito: apenas PROFESSOR que supervisiona o aluno.
     * 
     * @param id ID da medida a ser removida
     * @return ResponseEntity com status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR') and @medidasService.isMedidaDoProfessor(#id)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        medidasService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
