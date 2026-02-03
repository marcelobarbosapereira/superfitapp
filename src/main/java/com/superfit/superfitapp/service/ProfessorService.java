package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.professor.ProfessorCreateDTO;
import com.superfit.superfitapp.dto.professor.ProfessorResponseDTO;
import com.superfit.superfitapp.dto.professor.ProfessorUpdateDTO;

import java.util.List;

public interface ProfessorService {

	boolean isProfessorDoToken(Long professorId);

	ProfessorResponseDTO criar(ProfessorCreateDTO dto);

	List<ProfessorResponseDTO> listarTodos();

	ProfessorResponseDTO buscarPorId(Long id);

	ProfessorResponseDTO atualizar(Long id, ProfessorUpdateDTO dto);

	void remover(Long id);
}
