package com.superfit.superfitapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "exercicios")
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "repeticoes")
    private String repeticoes;

    @Column(name = "carga")
    private String carga;

    @Column(name = "grupo_muscular")
    private String grupoMuscular;

    @Column(name = "descanso_indicado")
    private String descansoIndicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id")
    private Treino treino;

    /* ===== Constructors ===== */

    public Exercicio() {}

    public Exercicio(String nome, String repeticoes, String carga, String grupoMuscular, String descansoIndicado) {
        this.nome = nome;
        this.repeticoes = repeticoes;
        this.carga = carga;
        this.grupoMuscular = grupoMuscular;
        this.descansoIndicado = descansoIndicado;
    }

    /* ===== Getters & Setters ===== */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(String repeticoes) {
        this.repeticoes = repeticoes;
    }

    public String getCarga() {
        return carga;
    }

    public void setCarga(String carga) {
        this.carga = carga;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public String getDescansoIndicado() {
        return descansoIndicado;
    }

    public void setDescansoIndicado(String descansoIndicado) {
        this.descansoIndicado = descansoIndicado;
    }

    public Treino getTreino() {
        return treino;
    }

    public void setTreino(Treino treino) {
        this.treino = treino;
    }
}
