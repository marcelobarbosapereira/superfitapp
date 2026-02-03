package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.aluno.AlunoCreateDTO;
import com.superfit.superfitapp.dto.aluno.AlunoResponseDTO;
import com.superfit.superfitapp.dto.aluno.AlunoUpdateDTO;

import java.util.List;

public interface AlunoService {

    boolean isAlunoDoToken(Long alunoId);

    boolean isAlunoDoProfessor(Long alunoId);

    AlunoResponseDTO criar(AlunoCreateDTO dto);

    List<AlunoResponseDTO> listar();

    AlunoResponseDTO buscarPorId(Long id);

    AlunoResponseDTO atualizar(Long id, AlunoUpdateDTO dto);

    void remover(Long id);
}