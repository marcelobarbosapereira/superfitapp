package com.superfit.superfitapp.dto.treino;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TreinoResponseDTO {

    private Long id;
    private String nome;
    private Long professorId;
    private String professorNome;
    private Long alunoId;
    private String alunoNome;
    private List<ExercicioDTO> exercicios;
}
