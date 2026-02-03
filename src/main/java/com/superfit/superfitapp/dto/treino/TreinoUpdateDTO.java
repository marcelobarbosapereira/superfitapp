package com.superfit.superfitapp.dto.treino;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TreinoUpdateDTO {

    private String nome;
    private List<ExercicioDTO> exercicios;
}
