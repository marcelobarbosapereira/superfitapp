package com.superfit.superfitapp.dto.mensalidade;

import com.superfit.superfitapp.model.StatusMensalidade;
import java.time.LocalDate;

public class MensalidadeResponseDTO {

    private Long id;
    private Long alunoId;
    private String alunoNome;
    private Double valor;
    private StatusMensalidade status;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private String mesReferencia;
    private Integer anoReferencia;
    private LocalDate dataCriacao;
    private String observacoes;

    /* ===== Constructors ===== */

    public MensalidadeResponseDTO() {}

    public MensalidadeResponseDTO(Long id, Long alunoId, String alunoNome, Double valor,
                                 StatusMensalidade status, LocalDate dataVencimento,
                                 LocalDate dataPagamento, String mesReferencia,
                                 Integer anoReferencia, LocalDate dataCriacao, String observacoes) {
        this.id = id;
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.valor = valor;
        this.status = status;
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.mesReferencia = mesReferencia;
        this.anoReferencia = anoReferencia;
        this.dataCriacao = dataCriacao;
        this.observacoes = observacoes;
    }

    /* ===== Getters & Setters ===== */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public void setAlunoNome(String alunoNome) {
        this.alunoNome = alunoNome;
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

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
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

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
