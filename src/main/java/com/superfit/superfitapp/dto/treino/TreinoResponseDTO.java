package com.superfit.superfitapp.dto.treino;

import java.util.List;

public class TreinoResponseDTO {

    private Long id;
    private String nome;
    private Long professorId;
    private String professorNome;
    private Long alunoId;
    private String alunoNome;
    private List<ExercicioDTO> exercicios;

    public TreinoResponseDTO() {
    }

    public TreinoResponseDTO(Long id, String nome, Long professorId, String professorNome, Long alunoId, String alunoNome, List<ExercicioDTO> exercicios) {
        this.id = id;
        this.nome = nome;
        this.professorId = professorId;
        this.professorNome = professorNome;
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.exercicios = exercicios;
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

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public String getProfessorNome() {
        return professorNome;
    }

    public void setProfessorNome(String professorNome) {
        this.professorNome = professorNome;
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

    public List<ExercicioDTO> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<ExercicioDTO> exercicios) {
        this.exercicios = exercicios;
    }
}
