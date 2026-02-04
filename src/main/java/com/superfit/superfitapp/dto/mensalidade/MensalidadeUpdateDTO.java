package com.superfit.superfitapp.dto.mensalidade;

import com.superfit.superfitapp.model.StatusMensalidade;
import java.time.LocalDate;

public class MensalidadeUpdateDTO {

    private Double valor;
    private StatusMensalidade status;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private String observacoes;

    /* ===== Constructors ===== */

    public MensalidadeUpdateDTO() {}

    /* ===== Getters & Setters ===== */

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public StatusMensalidade getStatus() {
        return status;
    }

    public void setStatus(StatusMensalidade status) {
        this.status = status;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
