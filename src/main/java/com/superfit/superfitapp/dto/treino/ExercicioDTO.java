package com.superfit.superfitapp.dto.treino;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExercicioDTO {

    private Long id;
    private String nome;
    private String repeticoes;
    private String carga;
    private String grupoMuscular;
    private String descansoIndicado;
}
