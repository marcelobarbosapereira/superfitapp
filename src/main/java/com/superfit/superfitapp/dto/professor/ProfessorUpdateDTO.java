package com.superfitapp.dto.professor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorUpdateDTO {

    private String nome;
    private String telefone;
    private Boolean ativo;
}
