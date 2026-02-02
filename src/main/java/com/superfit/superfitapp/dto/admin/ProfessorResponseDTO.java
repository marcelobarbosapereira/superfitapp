package com.superfit.superfitapp.dto.admin;

public record ProfessorResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String especialidade,
        Boolean ativo
) {}
