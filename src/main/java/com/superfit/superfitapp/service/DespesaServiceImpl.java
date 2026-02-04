package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.despesa.DespesaCreateDTO;
import com.superfit.superfitapp.dto.despesa.DespesaResponseDTO;
import com.superfit.superfitapp.dto.despesa.DespesaUpdateDTO;
import com.superfit.superfitapp.model.CategoriaDespesa;
import com.superfit.superfitapp.model.Despesa;
import com.superfit.superfitapp.model.StatusMensalidade;
import com.superfit.superfitapp.repository.DespesaRepository;
import com.superfit.superfitapp.repository.MensalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DespesaServiceImpl implements DespesaService {

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private MensalidadeRepository mensalidadeRepository;

    /**
     * Cria e persiste uma despesa com base nos dados do DTO.
     * Campos opcionais são tratados com valores padrão quando necessário.
     */
    @Override
    public DespesaResponseDTO criar(DespesaCreateDTO dto) {
        Despesa despesa = new Despesa();
        despesa.setDescricao(dto.getDescricao());
        despesa.setValor(dto.getValor());
        despesa.setCategoria(dto.getCategoria());
        despesa.setDataDespesa(dto.getDataDespesa());
        despesa.setDataPagamento(dto.getDataPagamento());
        despesa.setPaga(dto.getPaga() != null ? dto.getPaga() : false);
        despesa.setObservacoes(dto.getObservacoes());
        despesa.setDataCriacao(LocalDate.now());

        Despesa salva = despesaRepository.save(despesa);
        return toDTO(salva);
    }

    /**
     * Retorna todas as despesas mapeadas para DTO.
     */
    @Override
    public List<DespesaResponseDTO> listar() {
        return despesaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca despesa por ID com validação de existência.
     */
    @Override
    public DespesaResponseDTO buscarPorId(Long id) {
        Optional<Despesa> despesa = despesaRepository.findById(id);
        if (despesa.isEmpty()) {
            throw new IllegalArgumentException("Despesa não encontrada com ID: " + id);
        }
        return toDTO(despesa.get());
    }

    /**
     * Atualiza somente campos presentes no DTO para evitar sobrescrever valores.
     */
    @Override
    public DespesaResponseDTO atualizar(Long id, DespesaUpdateDTO dto) {
        Optional<Despesa> despesa = despesaRepository.findById(id);
        if (despesa.isEmpty()) {
            throw new IllegalArgumentException("Despesa não encontrada com ID: " + id);
        }

        Despesa d = despesa.get();
        if (dto.getDescricao() != null) {
            d.setDescricao(dto.getDescricao());
        }
        if (dto.getValor() != null) {
            d.setValor(dto.getValor());
        }
        if (dto.getCategoria() != null) {
            d.setCategoria(dto.getCategoria());
        }
        if (dto.getDataDespesa() != null) {
            d.setDataDespesa(dto.getDataDespesa());
        }
        if (dto.getDataPagamento() != null) {
            d.setDataPagamento(dto.getDataPagamento());
        }
        if (dto.getPaga() != null) {
            d.setPaga(dto.getPaga());
        }
        if (dto.getObservacoes() != null) {
            d.setObservacoes(dto.getObservacoes());
        }

        Despesa atualizada = despesaRepository.save(d);
        return toDTO(atualizada);
    }

    /**
     * Remove a despesa, validando previamente a existência.
     */
    @Override
    public void remover(Long id) {
        if (!despesaRepository.existsById(id)) {
            throw new IllegalArgumentException("Despesa não encontrada com ID: " + id);
        }
        despesaRepository.deleteById(id);
    }

    /**
     * Lista despesas por período, ordenando por data de despesa.
     */
    @Override
    public List<DespesaResponseDTO> listarPorPeriodo(String inicio, String fim) {
        LocalDate dataInicio = LocalDate.parse(inicio);
        LocalDate dataFim = LocalDate.parse(fim);
        return despesaRepository.findByDataDespesaBetweenOrderByDataDespesaDesc(dataInicio, dataFim).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista despesas pendentes (paga = false).
     */
    @Override
    public List<DespesaResponseDTO> listarPendentes() {
        return despesaRepository.findByPaga(false).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marca uma despesa como paga e registra a data atual.
     */
    @Override
    public void marcarComoPaga(Long id) {
        Optional<Despesa> despesa = despesaRepository.findById(id);
        if (despesa.isEmpty()) {
            throw new IllegalArgumentException("Despesa não encontrada com ID: " + id);
        }

        Despesa d = despesa.get();
        d.setPaga(true);
        d.setDataPagamento(LocalDate.now());
        despesaRepository.save(d);
    }

    /**
     * Soma todas as despesas no período informado.
     */
    @Override
    public Double obterTotalDespesasPorPeriodo(String inicio, String fim) {
        LocalDate dataInicio = LocalDate.parse(inicio);
        LocalDate dataFim = LocalDate.parse(fim);
        return despesaRepository.somarDespesasPorPeriodo(dataInicio, dataFim);
    }

    /**
     * Soma apenas despesas pagas no período informado.
     */
    @Override
    public Double obterTotalDespesasPagasPorPeriodo(String inicio, String fim) {
        LocalDate dataInicio = LocalDate.parse(inicio);
        LocalDate dataFim = LocalDate.parse(fim);
        return despesaRepository.somarDespesasPagasPorPeriodo(dataInicio, dataFim);
    }

    /**
     * Soma apenas despesas pendentes no período informado.
     */
    @Override
    public Double obterTotalDespesasPendentesPorPeriodo(String inicio, String fim) {
        LocalDate dataInicio = LocalDate.parse(inicio);
        LocalDate dataFim = LocalDate.parse(fim);
        return despesaRepository.somarDespesasPendentesPorPeriodo(dataInicio, dataFim);
    }

    /**
     * Gera o relatório mensal consolidando receitas e despesas do mês.
     * Receita vem de mensalidades pagas; lucro considera apenas despesas pagas.
     */
    @Override
    public Map<String, Object> obterRelatorioMensalCompleto(YearMonth mes) {
        LocalDate primeirodia = mes.atDay(1);
        LocalDate ultimoDia = mes.atEndOfMonth();

        // Receitas: mensalidades pagas dentro do mês.
        Double receitas = mensalidadeRepository.findByStatus(StatusMensalidade.PAGA).stream()
                .filter(m -> !m.getDataPagamento().isBefore(primeirodia) && 
                            !m.getDataPagamento().isAfter(ultimoDia))
                .mapToDouble(m -> m.getValor())
                .sum();

        // Despesas: total, pagas e pendentes para o mês.
        Double despesas = despesaRepository.somarDespesasPorPeriodo(primeirodia, ultimoDia);
        Double despesasPagas = despesaRepository.somarDespesasPagasPorPeriodo(primeirodia, ultimoDia);
        Double despesasPendentes = despesaRepository.somarDespesasPendentesPorPeriodo(primeirodia, ultimoDia);

        // Lucro: receitas menos despesas pagas.
        Double lucro = receitas - despesasPagas;

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("mes", mes.toString());
        relatorio.put("receitas", receitas);
        relatorio.put("despesasTotal", despesas);
        relatorio.put("despesasPagas", despesasPagas);
        relatorio.put("despesasPendentes", despesasPendentes);
        relatorio.put("lucro", lucro);
        relatorio.put("margem", receitas > 0 ? (lucro / receitas) * 100 : 0);

        return relatorio;
    }

    /**
     * Lista despesas filtradas por categoria específica.
     */
    @Override
    public List<DespesaResponseDTO> listarPorCategoria(CategoriaDespesa categoria) {
        return despesaRepository.findByCategoria(categoria).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte a entidade de domínio para DTO de resposta.
     */
    private DespesaResponseDTO toDTO(Despesa despesa) {
        return new DespesaResponseDTO(
                despesa.getId(),
                despesa.getDescricao(),
                despesa.getValor(),
                despesa.getCategoria(),
                despesa.getDataDespesa(),
                despesa.getDataPagamento(),
                despesa.getPaga(),
                despesa.getObservacoes(),
                despesa.getDataCriacao()
        );
    }
}
