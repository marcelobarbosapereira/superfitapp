package com.superfit.superfitapp.dto.medidas;

import java.time.LocalDate;

public class MedidasUpdateDTO {

    private LocalDate data;
    private Double peso;
    private Double peito;
    private Double cintura;
    private Double quadril;

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
}
