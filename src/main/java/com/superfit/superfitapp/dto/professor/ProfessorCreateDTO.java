package com.superfit.superfitapp.dto.professor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorCreateDTO {

    private String nome;
    private String email;
    private String senha;
    private String telefone;
}
