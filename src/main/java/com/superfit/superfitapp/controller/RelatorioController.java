package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.model.StatusMensalidade;
import com.superfit.superfitapp.repository.MensalidadeRepository;
import com.superfit.superfitapp.service.DespesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final DespesaService despesaService;
    private final MensalidadeRepository mensalidadeRepository;

    public RelatorioController(DespesaService despesaService, MensalidadeRepository mensalidadeRepository) {
        this.despesaService = despesaService;
        this.mensalidadeRepository = mensalidadeRepository;
    }

    /**
     * Relatório Financeiro Mensal Completo
     * Acesso: ADMIN / GESTOR
     * 
     * @param ano Ano (ex: 2026)
     * @param mes Mês (ex: 2 para fevereiro)
        * Usa o serviço de despesas para consolidar receitas, despesas e lucro.
     */
    @GetMapping("/financeiro/{ano}/{mes}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Map<String, Object>> relatorioFinanceiroMensal(
            @PathVariable Integer ano,
            @PathVariable Integer mes
    ) {
        YearMonth yearMonth = YearMonth.of(ano, mes);
        Map<String, Object> relatorio = despesaService.obterRelatorioMensalCompleto(yearMonth);
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório de Inadimplência
     * Acesso: ADMIN / GESTOR
     * 
     * Retorna lista de alunos com mensalidades pendentes
        * A lógica agrega pendências e calcula totais e contadores.
     */
    @GetMapping("/inadimplencia")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Map<String, Object>> relatorioInadimplencia() {
        Map<String, Object> relatorio = new HashMap<>();

        // Mensalidades pendentes são mapeadas para um formato resumido.
        List<Map<String, Object>> mensalidadesPendentes = mensalidadeRepository
                .findByStatus(StatusMensalidade.PENDENTE)
                .stream()
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", m.getId());
                    map.put("alunoId", m.getAluno().getId());
                    map.put("alunoNome", m.getAluno().getNome());
                    map.put("alunoEmail", m.getAluno().getEmail());
                    map.put("alunoCelular", m.getAluno().getTelefone());
                    map.put("valor", m.getValor());
                    map.put("mesReferencia", m.getMesReferencia());
                    map.put("anoReferencia", m.getAnoReferencia());
                    map.put("dataVencimento", m.getDataVencimento());
                    // Dias de atraso calculados com base na data atual.
                    map.put("diasAtraso", java.time.temporal.ChronoUnit.DAYS.between(m.getDataVencimento(), LocalDate.now()));
                    return map;
                })
                .collect(Collectors.toList());

        // Estatísticas consolidadas do relatório.
        Double totalInadimplencia = mensalidadesPendentes.stream()
                .mapToDouble(m -> (Double) m.get("valor"))
                .sum();

        long quantidadeAlunos = mensalidadesPendentes.stream()
                .map(m -> m.get("alunoId"))
                .distinct()
                .count();

        relatorio.put("quantidadeMensalidades", mensalidadesPendentes.size());
        relatorio.put("quantidadeAlunos", quantidadeAlunos);
        relatorio.put("totalInadimplencia", totalInadimplencia);
        relatorio.put("mensalidades", mensalidadesPendentes);

        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório de Inadimplência por Aluno
     * Acesso: ADMIN / GESTOR
     * 
     * @param alunoId ID do aluno
        * Filtra pendências do aluno e calcula total devido.
     */
    @GetMapping("/inadimplencia/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Map<String, Object>> relatorioInadimplenciaAluno(
            @PathVariable Long alunoId
    ) {
        Map<String, Object> relatorio = new HashMap<>();

        List<Map<String, Object>> mensalidadesPendentes = mensalidadeRepository
                .findByAlunoIdAndStatus(alunoId, StatusMensalidade.PENDENTE)
                .stream()
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", m.getId());
                    map.put("valor", m.getValor());
                    map.put("mesReferencia", m.getMesReferencia());
                    map.put("anoReferencia", m.getAnoReferencia());
                    map.put("dataVencimento", m.getDataVencimento());
                                        // Dias de atraso calculados para cada mensalidade.
                    map.put("diasAtraso", java.time.temporal.ChronoUnit.DAYS.between(m.getDataVencimento(), LocalDate.now()));
                    return map;
                })
                .collect(Collectors.toList());

        Double totalDevido = mensalidadesPendentes.stream()
                .mapToDouble(m -> (Double) m.get("valor"))
                .sum();

        relatorio.put("alunoId", alunoId);
        relatorio.put("quantidadeMensalidades", mensalidadesPendentes.size());
        relatorio.put("totalDevido", totalDevido);
        relatorio.put("mensalidades", mensalidadesPendentes);

        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório de Receitas por Período
     * Acesso: ADMIN / GESTOR
        * Soma mensalidades pagas e calcula ticket médio no intervalo.
     */
    @GetMapping("/receitas")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Map<String, Object>> relatorioReceitas(
            @RequestParam String inicio,
            @RequestParam String fim
    ) {
        Map<String, Object> relatorio = new HashMap<>();

        LocalDate dataInicio = LocalDate.parse(inicio);
        LocalDate dataFim = LocalDate.parse(fim);

        Double receitas = mensalidadeRepository.findByStatus(StatusMensalidade.PAGA).stream()
                .filter(m -> !m.getDataPagamento().isBefore(dataInicio) && 
                            !m.getDataPagamento().isAfter(dataFim))
                .mapToDouble(m -> m.getValor())
                .sum();

        long quantidadePagamentos = mensalidadeRepository.findByStatus(StatusMensalidade.PAGA).stream()
                .filter(m -> !m.getDataPagamento().isBefore(dataInicio) && 
                            !m.getDataPagamento().isAfter(dataFim))
                .count();

        relatorio.put("periodo", inicio + " a " + fim);
        relatorio.put("totalReceitas", receitas);
        relatorio.put("quantidadePagamentos", quantidadePagamentos);
        relatorio.put("ticketMedio", quantidadePagamentos > 0 ? receitas / quantidadePagamentos : 0);

        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório Comparativo: Receitas vs Despesas
     * Acesso: ADMIN / GESTOR
        * Reaproveita o relatório mensal consolidado do serviço de despesas.
     */
    @GetMapping("/comparativo/{ano}/{mes}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Map<String, Object>> relatorioComparativo(
            @PathVariable Integer ano,
            @PathVariable Integer mes
    ) {
        YearMonth yearMonth = YearMonth.of(ano, mes);
        Map<String, Object> relatorio = despesaService.obterRelatorioMensalCompleto(yearMonth);
        return ResponseEntity.ok(relatorio);
    }
}
