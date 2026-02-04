package com.superfit.superfitapp.dto.despesa;

import com.superfit.superfitapp.model.CategoriaDespesa;
import java.time.LocalDate;

public class DespesaResponseDTO {

    private Long id;
    private String descricao;
    private Double valor;
    private CategoriaDespesa categoria;
    private LocalDate dataDespesa;
    private LocalDate dataPagamento;
    private Boolean paga;
    private String observacoes;
    private LocalDate dataCriacao;

    /* ===== Constructors ===== */

    public DespesaResponseDTO() {}

    public DespesaResponseDTO(Long id, String descricao, Double valor, CategoriaDespesa categoria,
                            LocalDate dataDespesa, LocalDate dataPagamento, Boolean paga,
                            String observacoes, LocalDate dataCriacao) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.categoria = categoria;
        this.dataDespesa = dataDespesa;
        this.dataPagamento = dataPagamento;
        this.paga = paga;
        this.observacoes = observacoes;
        this.dataCriacao = dataCriacao;
    }

    /* ===== Getters & Setters ===== */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public CategoriaDespesa getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDespesa categoria) {
        this.categoria = categoria;
    }

    public LocalDate getDataDespesa() {
        return dataDespesa;
    }

    public void setDataDespesa(LocalDate dataDespesa) {
        this.dataDespesa = dataDespesa;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Boolean getPaga() {
        return paga;
    }

    public void setPaga(Boolean paga) {
        this.paga = paga;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
