package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.treino.TreinoCreateDTO;
import com.superfit.superfitapp.dto.treino.TreinoResponseDTO;
import com.superfit.superfitapp.dto.treino.TreinoUpdateDTO;

import java.util.List;

public interface TreinoService {

    boolean isTreinoDoProfessor(Long treinoId);

    boolean isTreinoDoAluno(Long treinoId);

    TreinoResponseDTO criar(TreinoCreateDTO dto);

    List<TreinoResponseDTO> listarTodos();

    TreinoResponseDTO buscarPorId(Long id);

    TreinoResponseDTO atualizar(Long id, TreinoUpdateDTO dto);

    void remover(Long id);
}
