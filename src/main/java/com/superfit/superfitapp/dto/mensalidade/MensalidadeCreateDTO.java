package com.superfit.superfitapp.dto.mensalidade;

import com.superfit.superfitapp.model.StatusMensalidade;
import java.time.LocalDate;

public class MensalidadeCreateDTO {

    private Long alunoId;
    private Double valor;
    private StatusMensalidade status;
    private LocalDate dataVencimento;
    private String mesReferencia;
    private Integer anoReferencia;
    private String observacoes;

    /* ===== Constructors ===== */

    public MensalidadeCreateDTO() {}

    public MensalidadeCreateDTO(Long alunoId, Double valor, StatusMensalidade status,
                               LocalDate dataVencimento, String mesReferencia, Integer anoReferencia) {
        this.alunoId = alunoId;
        this.valor = valor;
        this.status = status;
        this.dataVencimento = dataVencimento;
        this.mesReferencia = mesReferencia;
        this.anoReferencia = anoReferencia;
    }

    /* ===== Getters & Setters ===== */

    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

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

    public String getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(String mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public Integer getAnoReferencia() {
        return anoReferencia;
    }

    public void setAnoReferencia(Integer anoReferencia) {
        this.anoReferencia = anoReferencia;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
