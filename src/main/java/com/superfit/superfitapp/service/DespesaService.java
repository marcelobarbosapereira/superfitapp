package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.despesa.DespesaCreateDTO;
import com.superfit.superfitapp.dto.despesa.DespesaResponseDTO;
import com.superfit.superfitapp.dto.despesa.DespesaUpdateDTO;
import com.superfit.superfitapp.model.CategoriaDespesa;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface DespesaService {

    /**
     * Cria uma nova despesa a partir dos dados recebidos no DTO.
     * A lógica de persistência fica na implementação do serviço.
     */
    DespesaResponseDTO criar(DespesaCreateDTO dto);

    /**
     * Lista todas as despesas cadastradas.
     */
    List<DespesaResponseDTO> listar();

    /**
     * Busca uma despesa pelo ID; lança exceção se não existir.
     */
    DespesaResponseDTO buscarPorId(Long id);

    /**
     * Atualiza apenas os campos não nulos da despesa informada.
     */
    DespesaResponseDTO atualizar(Long id, DespesaUpdateDTO dto);

    /**
     * Remove uma despesa existente pelo ID.
     */
    void remover(Long id);

    /**
     * Lista despesas dentro de um período (datas em formato ISO).
     */
    List<DespesaResponseDTO> listarPorPeriodo(String inicio, String fim);

    /**
     * Lista despesas que ainda não foram pagas.
     */
    List<DespesaResponseDTO> listarPendentes();

    /**
     * Marca uma despesa como paga e registra a data de pagamento.
     */
    void marcarComoPaga(Long id);

    /**
     * Soma o total de despesas em um período.
     */
    Double obterTotalDespesasPorPeriodo(String inicio, String fim);

    /**
     * Soma apenas despesas pagas em um período.
     */
    Double obterTotalDespesasPagasPorPeriodo(String inicio, String fim);

    /**
     * Soma apenas despesas pendentes em um período.
     */
    Double obterTotalDespesasPendentesPorPeriodo(String inicio, String fim);

    /**
     * Gera um relatório mensal consolidando receitas, despesas e lucro.
     */
    Map<String, Object> obterRelatorioMensalCompleto(YearMonth mes);

    /**
     * Lista despesas filtradas por categoria.
     */
    List<DespesaResponseDTO> listarPorCategoria(CategoriaDespesa categoria);
}
