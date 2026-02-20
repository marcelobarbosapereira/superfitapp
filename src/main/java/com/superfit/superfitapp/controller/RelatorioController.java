package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.model.StatusMensalidade;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.DespesaRepository;
import com.superfit.superfitapp.repository.MensalidadeRepository;
import com.superfit.superfitapp.repository.ProfessorRepository;
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
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final DespesaRepository despesaRepository;

    public RelatorioController(DespesaService despesaService, MensalidadeRepository mensalidadeRepository,
                              AlunoRepository alunoRepository, ProfessorRepository professorRepository,
                              DespesaRepository despesaRepository) {
        this.despesaService = despesaService;
        this.mensalidadeRepository = mensalidadeRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.despesaRepository = despesaRepository;
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

    /**
     * Relatório de Alunos Ativos (sem inadimplência)
     * Acesso: ADMIN / GESTOR
     */
    @GetMapping("/alunos-ativos")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Map<String, Object>> relatorioAlunosAtivos() {
        Map<String, Object> relatorio = new HashMap<>();
        
        long totalAlunos = alunoRepository.count();
        
        // Alunos sem mensalidades pendentes
        List<Map<String, Object>> alunosAtivos = alunoRepository.findAll().stream()
                .filter(aluno -> {
                    long pendentes = mensalidadeRepository.countByAlunoIdAndStatus(aluno.getId(), StatusMensalidade.PENDENTE);
                    return pendentes == 0;
                })
                .map(aluno -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", aluno.getId());
                    map.put("nome", aluno.getNome());
                    map.put("email", aluno.getEmail());
                    map.put("telefone", aluno.getTelefone());
                    return map;
                })
                .collect(Collectors.toList());
        
        relatorio.put("totalAlunos", totalAlunos);
        relatorio.put("alunosAtivos", alunosAtivos.size());
        relatorio.put("listaAtivos", alunosAtivos);
        relatorio.put("percentualAtivos", totalAlunos > 0 ? (alunosAtivos.size() * 100.0 / totalAlunos) : 0);
        
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório de Professores com estatísticas
     * Acesso: ADMIN / GESTOR
     */
    @GetMapping("/professores")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Map<String, Object>> relatorioProfessores() {
        Map<String, Object> relatorio = new HashMap<>();
        
        List<Map<String, Object>> statsProfessores = professorRepository.findAll().stream()
                .map(prof -> {
                    long qtdAlunos = alunoRepository.findAll().stream()
                            .filter(aluno -> aluno.getProfessor() != null && 
                                           aluno.getProfessor().getId().equals(prof.getId()))
                            .count();
                    
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("professorId", prof.getId());
                    stats.put("professorNome", prof.getNome());
                    stats.put("professorEmail", prof.getEmail());
                    stats.put("quantidadeAlunos", qtdAlunos);
                    return stats;
                })
                .collect(Collectors.toList());
        
        long totalAlunos = alunoRepository.count();
        long totalProfessores = professorRepository.count();
        double mediaAlunosPorProfessor = totalProfessores > 0 ? (double) totalAlunos / totalProfessores : 0;
        
        relatorio.put("totalProfessores", totalProfessores);
        relatorio.put("totalAlunos", totalAlunos);
        relatorio.put("mediaAlunosPorProfessor", mediaAlunosPorProfessor);
        relatorio.put("listaProfessores", statsProfessores);
        
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório de Receita Mensal Simplificado
     * Acesso: ADMIN / GESTOR
     */
    @GetMapping("/receita-mensal")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Map<String, Object>> receitaMensal() {
        Map<String, Object> relatorio = new HashMap<>();
        
        // Total de mensalidades
        long totalMensalidades = mensalidadeRepository.count();
        
        // Mensalidades pagas
        long totalPagas = mensalidadeRepository.findByStatus(StatusMensalidade.PAGA).size();
        
        // Mensalidades pendentes
        long totalPendentes = mensalidadeRepository.findByStatus(StatusMensalidade.PENDENTE).size();
        
        // Receita realizada
        double receitaRealizada = mensalidadeRepository.findByStatus(StatusMensalidade.PAGA).stream()
                .mapToDouble(m -> m.getValor())
                .sum();
        
        // Receita prevista
        double receitaPrevista = mensalidadeRepository.findAll().stream()
                .mapToDouble(m -> m.getValor())
                .sum();
        
        // Receita pendente
        double receitaPendente = mensalidadeRepository.findByStatus(StatusMensalidade.PENDENTE).stream()
                .mapToDouble(m -> m.getValor())
                .sum();
        
        relatorio.put("totalMensalidades", totalMensalidades);
        relatorio.put("totalPagas", totalPagas);
        relatorio.put("totalPendentes", totalPendentes);
        relatorio.put("receitaRealizada", receitaRealizada);
        relatorio.put("receitaPrevista", receitaPrevista);
        relatorio.put("receitaPendente", receitaPendente);
        relatorio.put("taxaAdimplencia", totalMensalidades > 0 ? (totalPagas * 100.0 / totalMensalidades) : 0);
        
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório de Despesas Mensais Simplificado
     * Acesso: ADMIN / GESTOR
     */
    @GetMapping("/despesas-mensais")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Map<String, Object>> despesasMensais() {
        Map<String, Object> relatorio = new HashMap<>();
        
        // Total de despesas
        long totalDespesas = despesaRepository.count();
        
        // Despesas pagas
        long totalPagas = despesaRepository.findByPaga(true).size();
        
        // Despesas pendentes
        long totalPendentes = despesaRepository.findByPaga(false).size();
        
        // Total de despesas pagas
        double despesasPagas = despesaRepository.findByPaga(true).stream()
                .mapToDouble(d -> d.getValor())
                .sum();
        
        // Total de despesas previstas
        double despesasTotal = despesaRepository.findAll().stream()
                .mapToDouble(d -> d.getValor())
                .sum();
        
        // Total de despesas pendentes
        double despesasPendentes = despesaRepository.findByPaga(false).stream()
                .mapToDouble(d -> d.getValor())
                .sum();
        
        relatorio.put("totalDespesas", totalDespesas);
        relatorio.put("totalPagas", totalPagas);
        relatorio.put("totalPendentes", totalPendentes);
        relatorio.put("despesasPagas", despesasPagas);
        relatorio.put("despesasTotal", despesasTotal);
        relatorio.put("despesasPendentes", despesasPendentes);
        relatorio.put("taxaPagamento", totalDespesas > 0 ? (totalPagas * 100.0 / totalDespesas) : 0);
        
        return ResponseEntity.ok(relatorio);
    }
}
