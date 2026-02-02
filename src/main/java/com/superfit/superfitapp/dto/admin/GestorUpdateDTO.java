package com.superfit.superfitapp.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GestorUpdateDTO(

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String password
) {}
