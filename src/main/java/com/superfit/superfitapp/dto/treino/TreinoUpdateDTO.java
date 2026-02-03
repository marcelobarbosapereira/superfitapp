package com.superfit.superfitapp.dto.treino;

import java.util.List;

public class TreinoUpdateDTO {

    private String nome;
    private List<ExercicioDTO> exercicios;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<ExercicioDTO> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<ExercicioDTO> exercicios) {
        this.exercicios = exercicios;
    }
}
