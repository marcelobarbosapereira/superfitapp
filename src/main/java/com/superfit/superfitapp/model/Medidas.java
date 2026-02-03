package com.superfit.superfitapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medidas")
public class Medidas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private Double peso;

    @Column(name = "imc")
    private Double imc;

    @Column(name = "peito")
    private Double peito;

    @Column(name = "cintura")
    private Double cintura;

    @Column(name = "quadril")
    private Double quadril;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    /* ===== Constructors ===== */

    public Medidas() {}

    public Medidas(LocalDate data, Double peso, Double peito, Double cintura, Double quadril, Aluno aluno) {
        this.data = data;
        this.peso = peso;
        this.peito = peito;
        this.cintura = cintura;
        this.quadril = quadril;
        this.aluno = aluno;
    }

    /* ===== Getters & Setters ===== */

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

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    /* ===== Business Methods ===== */

    /**
     * Calcula o IMC baseado no peso e altura do aluno
     */
    public void calcularImc() {
        if (this.peso != null && this.aluno != null && this.aluno.getAltura() != null && this.aluno.getAltura() > 0) {
            this.imc = this.peso / (this.aluno.getAltura() * this.aluno.getAltura());
            // Arredondar para 1 casa decimal
            this.imc = Math.round(this.imc * 10.0) / 10.0;
        }
    }
}
