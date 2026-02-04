package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.despesa.DespesaCreateDTO;
import com.superfit.superfitapp.dto.despesa.DespesaResponseDTO;
import com.superfit.superfitapp.dto.despesa.DespesaUpdateDTO;
import com.superfit.superfitapp.model.CategoriaDespesa;
import com.superfit.superfitapp.service.DespesaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {

    private final DespesaService despesaService;

    public DespesaController(DespesaService despesaService) {
        this.despesaService = despesaService;
    }

    /**
     * Criar despesa
     * Acesso: ADMIN / GESTOR
        * Recebe os dados no DTO e delega a criação ao serviço.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<DespesaResponseDTO> criar(
            @RequestBody DespesaCreateDTO dto
    ) {
        DespesaResponseDTO response = despesaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar todas as despesas
     * Acesso: ADMIN / GESTOR
        * Retorna a coleção completa de despesas em DTO.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<List<DespesaResponseDTO>> listar() {
        return ResponseEntity.ok(despesaService.listar());
    }

    /**
     * Buscar despesa por ID
     * Acesso: ADMIN / GESTOR
        * Valida existência da despesa na camada de serviço.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<DespesaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(despesaService.buscarPorId(id));
    }

    /**
     * Atualizar despesa
     * Acesso: ADMIN / GESTOR
        * Atualiza somente os campos presentes no DTO.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<DespesaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody DespesaUpdateDTO dto
    ) {
        DespesaResponseDTO response = despesaService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Remover despesa
     * Acesso: ADMIN / GESTOR
        * Exclui a despesa caso exista.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        despesaService.remover(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Listar despesas por período
     * Acesso: ADMIN / GESTOR
     * @param inicio Data no formato YYYY-MM-DD
     * @param fim Data no formato YYYY-MM-DD
        * Delega a filtragem ao serviço, que converte as datas.
     */
    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<List<DespesaResponseDTO>> listarPorPeriodo(
            @RequestParam String inicio,
            @RequestParam String fim
    ) {
        return ResponseEntity.ok(despesaService.listarPorPeriodo(inicio, fim));
    }

    /**
     * Listar despesas pendentes
     * Acesso: ADMIN / GESTOR
        * Retorna despesas com paga = false.
     */
    @GetMapping("/pendentes")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<List<DespesaResponseDTO>> listarPendentes() {
        return ResponseEntity.ok(despesaService.listarPendentes());
    }

    /**
     * Marcar despesa como paga
     * Acesso: ADMIN / GESTOR
        * Define paga = true e registra a data atual.
     */
    @PutMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> marcarComoPaga(@PathVariable Long id) {
        despesaService.marcarComoPaga(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obter total de despesas por período
     * Acesso: ADMIN / GESTOR
        * Soma despesas no período informado.
     */
    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Double> obterTotal(
            @RequestParam String inicio,
            @RequestParam String fim
    ) {
        return ResponseEntity.ok(despesaService.obterTotalDespesasPorPeriodo(inicio, fim));
    }

    /**
     * Listar despesas por categoria
     * Acesso: ADMIN / GESTOR
        * Filtra despesas pela categoria informada.
     */
    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<List<DespesaResponseDTO>> listarPorCategoria(
            @PathVariable CategoriaDespesa categoria
    ) {
        return ResponseEntity.ok(despesaService.listarPorCategoria(categoria));
    }
}
