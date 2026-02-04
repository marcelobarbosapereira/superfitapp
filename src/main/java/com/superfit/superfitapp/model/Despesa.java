package com.superfit.superfitapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "despesas")
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaDespesa categoria;

    @Column(name = "data_despesa", nullable = false)
    private LocalDate dataDespesa;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "paga")
    private Boolean paga = false;

    @Column(name = "observacoes")
    private String observacoes;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    /* ===== Constructors ===== */

    public Despesa() {
        this.dataCriacao = LocalDate.now();
    }

    public Despesa(String descricao, Double valor, CategoriaDespesa categoria, 
                   LocalDate dataDespesa) {
        this.descricao = descricao;
        this.valor = valor;
        this.categoria = categoria;
        this.dataDespesa = dataDespesa;
        this.dataCriacao = LocalDate.now();
        this.paga = false;
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

    @Override
    public String toString() {
        return "Despesa{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", categoria=" + categoria +
                ", dataDespesa=" + dataDespesa +
                ", paga=" + paga +
                '}';
    }
}
