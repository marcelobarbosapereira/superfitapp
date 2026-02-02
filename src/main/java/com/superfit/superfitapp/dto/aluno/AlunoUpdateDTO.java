package com.superfit.superfitapp.dto.aluno;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlunoUpdateDTO {

    private String nome;
    private String telefone;
    private Boolean ativo;
}
