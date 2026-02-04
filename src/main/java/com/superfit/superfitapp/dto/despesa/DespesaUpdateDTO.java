package com.superfit.superfitapp.dto.despesa;

import com.superfit.superfitapp.model.CategoriaDespesa;
import java.time.LocalDate;

public class DespesaUpdateDTO {

    private String descricao;
    private Double valor;
    private CategoriaDespesa categoria;
    private LocalDate dataDespesa;
    private LocalDate dataPagamento;
    private Boolean paga;
    private String observacoes;

    /* ===== Constructors ===== */

    public DespesaUpdateDTO() {}

    /* ===== Getters & Setters ===== */

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
}
