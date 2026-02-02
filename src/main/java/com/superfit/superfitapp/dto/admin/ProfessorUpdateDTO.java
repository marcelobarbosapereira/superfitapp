package com.superfit.superfitapp.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ProfessorUpdateDTO(

        String nome,

        @Email(message = "Email inválido")
        String email,

        String telefone,

        String especialidade,

        String password
) {}
