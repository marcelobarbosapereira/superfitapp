package com.superfit.superfitapp.dto.professor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfessorResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String crefi;
}
