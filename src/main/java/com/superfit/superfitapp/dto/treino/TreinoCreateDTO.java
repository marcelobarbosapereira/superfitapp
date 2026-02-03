package com.superfit.superfitapp.dto.treino;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TreinoCreateDTO {

    private String nome;
    private Long alunoId;
    private List<ExercicioDTO> exercicios;
}
