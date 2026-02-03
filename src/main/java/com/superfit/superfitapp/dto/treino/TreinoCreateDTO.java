package com.superfit.superfitapp.dto.treino;

import java.util.List;

public class TreinoCreateDTO {

    private String nome;
    private Long alunoId;
    private List<ExercicioDTO> exercicios;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public List<ExercicioDTO> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<ExercicioDTO> exercicios) {
        this.exercicios = exercicios;
    }
}
