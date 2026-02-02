package com.superfit.superfitapp.dto.admin;

public record AlunoResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String dataNascimento,
        Boolean ativo
) {}
