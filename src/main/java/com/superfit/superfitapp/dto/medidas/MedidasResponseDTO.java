package com.superfit.superfitapp.dto.medidas;

import java.time.LocalDate;

public class MedidasResponseDTO {

    private Long id;
    private LocalDate data;
    private Double peso;
    private Double imc;
    private Double peito;
    private Double cintura;
    private Double quadril;
    private Long alunoId;
    private String alunoNome;

    public MedidasResponseDTO() {
    }

    public MedidasResponseDTO(Long id, LocalDate data, Double peso, Double imc, Double peito, Double cintura, Double quadril, Long alunoId, String alunoNome) {
        this.id = id;
        this.data = data;
        this.peso = peso;
        this.imc = imc;
        this.peito = peito;
        this.cintura = cintura;
        this.quadril = quadril;
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getImc() {
        return imc;
    }

    public void setImc(Double imc) {
        this.imc = imc;
    }

    public Double getPeito() {
        return peito;
    }

    public void setPeito(Double peito) {
        this.peito = peito;
    }

    public Double getCintura() {
        return cintura;
    }

    public void setCintura(Double cintura) {
        this.cintura = cintura;
    }

    public Double getQuadril() {
        return quadril;
    }

    public void setQuadril(Double quadril) {
        this.quadril = quadril;
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
}
