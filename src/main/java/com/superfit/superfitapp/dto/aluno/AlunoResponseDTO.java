package com.superfit.superfitapp.dto.aluno;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlunoResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Boolean ativo;

    private Long professorId;
    private String professorNome;
}
