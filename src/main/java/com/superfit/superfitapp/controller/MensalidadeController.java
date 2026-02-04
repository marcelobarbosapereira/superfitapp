package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.mensalidade.MensalidadeCreateDTO;
import com.superfit.superfitapp.dto.mensalidade.MensalidadeResponseDTO;
import com.superfit.superfitapp.dto.mensalidade.MensalidadeUpdateDTO;
import com.superfit.superfitapp.model.StatusMensalidade;
import com.superfit.superfitapp.service.MensalidadeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Mensalidades dos alunos.
 * Expõe endpoints para CRUD de mensalidades e consultas por aluno/status.
 * 
 * Regras de acesso:
 * - ADMIN/GESTOR: acesso total a todas as mensalidades
 * - ALUNO: visualiza apenas suas próprias mensalidades
 */
@RestController
@RequestMapping("/api/mensalidades")
public class MensalidadeController {

    private final MensalidadeService mensalidadeService;

    public MensalidadeController(MensalidadeService mensalidadeService) {
        this.mensalidadeService = mensalidadeService;
    }

    /**
     * Cria uma nova mensalidade para um aluno.
     * Acesso restrito: apenas ADMIN ou GESTOR.
     * Define a data de criação como a data atual.
     * 
     * @param dto Dados da mensalidade (alunoId, valor, status, dataVencimento, mêsReferencia, anoReferencia)
     * @return ResponseEntity com status 201 e dados da mensalidade criada
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<MensalidadeResponseDTO> criar(
            @RequestBody MensalidadeCreateDTO dto
    ) {
        MensalidadeResponseDTO response = mensalidadeService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas as mensalidades cadastradas no sistema.
     * Acesso restrito: apenas ADMIN ou GESTOR.
     * 
     * @return ResponseEntity com lista de todas as mensalidades
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<List<MensalidadeResponseDTO>> listar() {
        return ResponseEntity.ok(mensalidadeService.listar());
    }

    /**
     * Lista mensalidades de um aluno específico.
     * Acesso: ADMIN, GESTOR ou ALUNO (suas próprias mensalidades).
     * Ordenadas em ordem decrescente de data de vencimento.
     * 
     * @param alunoId ID do aluno
     * @return ResponseEntity com lista de mensalidades do aluno
     */
    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR') or hasRole('ALUNO')")
    public ResponseEntity<List<MensalidadeResponseDTO>> listarPorAluno(
            @PathVariable Long alunoId
    ) {
        return ResponseEntity.ok(mensalidadeService.listarPorAluno(alunoId));
    }

    /**
     * Listar mensalidades de um aluno por status
     * Acesso: ADMIN / GESTOR / ALUNO (suas próprias mensalidades)
     */
    @GetMapping("/aluno/{alunoId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR') or hasRole('ALUNO')")
    public ResponseEntity<List<MensalidadeResponseDTO>> listarPorAlunoEStatus(
            @PathVariable Long alunoId,
            @PathVariable StatusMensalidade status
    ) {
        return ResponseEntity.ok(mensalidadeService.listarPorAlunoEStatus(alunoId, status));
    }

    /**
     * Buscar mensalidade por ID
     * Acesso: ADMIN / GESTOR
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<MensalidadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mensalidadeService.buscarPorId(id));
    }

    /**
     * Atualizar mensalidade
     * Acesso: ADMIN / GESTOR
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<MensalidadeResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody MensalidadeUpdateDTO dto
    ) {
        MensalidadeResponseDTO response = mensalidadeService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Remover mensalidade
     * Acesso: ADMIN / GESTOR
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        mensalidadeService.remover(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Listar todas as mensalidades pendentes
     * Acesso: ADMIN / GESTOR
     */
    @GetMapping("/pendentes")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<List<MensalidadeResponseDTO>> listarPendentes() {
        return ResponseEntity.ok(mensalidadeService.listarPendentes());
    }

    /**
     * Marcar mensalidade como paga
     * Acesso: ADMIN / GESTOR
     */
    @PutMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> marcarComoPaga(@PathVariable Long id) {
        mensalidadeService.marcarComoPaga(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Contar mensalidades pendentes de um aluno
     * Acesso: ADMIN / GESTOR / ALUNO (suas próprias)
     */
    @GetMapping("/aluno/{alunoId}/pendentes/count")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR') or hasRole('ALUNO')")
    public ResponseEntity<Long> countPendentesPorAluno(
            @PathVariable Long alunoId
    ) {
        return ResponseEntity.ok(mensalidadeService.countPendentesPorAluno(alunoId));
    }
}
