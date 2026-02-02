package com.superfit.superfitapp.dto.admin;

import jakarta.validation.constraints.Email;

public record AlunoUpdateDTO(

        String nome,

        @Email(message = "Email inválido")
        String email,

        String telefone,

        String dataNascimento,

        String password
) {}
