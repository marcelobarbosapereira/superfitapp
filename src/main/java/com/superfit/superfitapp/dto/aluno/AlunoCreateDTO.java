package com.superfit.superfitapp.dto.aluno;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlunoCreateDTO {

    private String nome;
    private String email;
    private String senha;
    private String telefone;

    private Long professorId;
}
