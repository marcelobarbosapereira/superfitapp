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

@RestController
@RequestMapping("/api/medidas")
public class MedidasController {

    private final MedidasService medidasService;

    public MedidasController(MedidasService medidasService) {
        this.medidasService = medidasService;
    }

    /**
     * Criar medidas
     * Acesso: PROFESSOR
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
     * Listar medidas
     * PROFESSOR → medidas de todos os seus alunos
     * ALUNO → apenas suas medidas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<MedidasResponseDTO>> listar() {
        return ResponseEntity.ok(medidasService.listarTodas());
    }

    /**
     * Listar medidas por aluno
     * PROFESSOR → apenas de seus alunos
     * ALUNO → apenas suas próprias medidas
     */
    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<MedidasResponseDTO>> listarPorAluno(
            @PathVariable Long alunoId
    ) {
        return ResponseEntity.ok(medidasService.listarPorAluno(alunoId));
    }

    /**
     * Obter histórico de evolução de um aluno
     * PROFESSOR → apenas de seus alunos
     * ALUNO → apenas seu próprio histórico
     */
    @GetMapping("/historico/{alunoId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public ResponseEntity<HistoricoEvolucaoDTO> obterHistorico(
            @PathVariable Long alunoId
    ) {
        return ResponseEntity.ok(medidasService.obterHistoricoEvolucao(alunoId));
    }

    /**
     * Buscar medidas por ID
     * PROFESSOR → apenas medidas de seus alunos
     * ALUNO → apenas suas medidas
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
     * Atualizar medidas
     * Acesso: PROFESSOR (apenas medidas de seus alunos)
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
     * Remover medidas
     * Acesso: PROFESSOR (apenas medidas de seus alunos)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR') and @medidasService.isMedidaDoProfessor(#id)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        medidasService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
