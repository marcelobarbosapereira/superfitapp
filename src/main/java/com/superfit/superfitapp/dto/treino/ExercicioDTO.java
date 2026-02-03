package com.superfit.superfitapp.dto.treino;

public class ExercicioDTO {

    private Long id;
    private String nome;
    private String repeticoes;
    private String carga;
    private String grupoMuscular;
    private String descansoIndicado;

    public ExercicioDTO() {
    }

    public ExercicioDTO(Long id, String nome, String repeticoes, String carga, String grupoMuscular, String descansoIndicado) {
        this.id = id;
        this.nome = nome;
        this.repeticoes = repeticoes;
        this.carga = carga;
        this.grupoMuscular = grupoMuscular;
        this.descansoIndicado = descansoIndicado;
    }

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
}
