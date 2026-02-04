package com.superfit.superfitapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mensalidades")
public class Mensalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMensalidade status;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "mes_referencia", nullable = false)
    private String mesReferencia;

    @Column(name = "ano_referencia", nullable = false)
    private Integer anoReferencia;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @Column(name = "observacoes")
    private String observacoes;

    /* ===== Constructors ===== */

    public Mensalidade() {
        this.dataCriacao = LocalDate.now();
    }

    public Mensalidade(Aluno aluno, Double valor, StatusMensalidade status, 
                      LocalDate dataVencimento, String mesReferencia, Integer anoReferencia) {
        this.aluno = aluno;
        this.valor = valor;
        this.status = status;
        this.dataVencimento = dataVencimento;
        this.mesReferencia = mesReferencia;
        this.anoReferencia = anoReferencia;
        this.dataCriacao = LocalDate.now();
    }

    /* ===== Getters & Setters ===== */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
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

    @Override
    public String toString() {
        return "Mensalidade{" +
                "id=" + id +
                ", aluno=" + aluno.getNome() +
                ", valor=" + valor +
                ", status=" + status +
                ", dataVencimento=" + dataVencimento +
                ", mesReferencia='" + mesReferencia + '\'' +
                ", anoReferencia=" + anoReferencia +
                '}';
    }
}
