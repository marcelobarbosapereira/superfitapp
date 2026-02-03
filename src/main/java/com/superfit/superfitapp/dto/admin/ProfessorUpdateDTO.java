package com.superfit.superfitapp.dto.admin;

import jakarta.validation.constraints.Email;

public record ProfessorUpdateDTO(

        String nome,

        @Email(message = "Email inválido")
        String email,

        String telefone,

        String crefi,

        String password
) {}
