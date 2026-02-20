package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.professor.ProfessorCreateDTO;
import com.superfit.superfitapp.dto.professor.ProfessorResponseDTO;
import com.superfit.superfitapp.dto.professor.ProfessorUpdateDTO;

import java.util.List;

/**
 * Interface de serviço para gerenciamento de Professores.
 * Define operações de CRUD e métodos de segurança para controle de acesso.
 */
public interface ProfessorService {

	/**
	 * Verifica se o professor pertence ao usuário autenticado no token JWT.
	 * 
	 * @param professorId ID do professor a ser verificado
	 * @return true se o professor pertence ao usuário do token, false caso contrário
	 */
	boolean isProfessorDoToken(Long professorId);

	/**
	 * Cria um novo professor no sistema.
	 * 
	 * @param dto Dados para criação do professor (nome, email, telefone, CREFI)
	 * @return DTO com os dados do professor criado
	 */
	ProfessorResponseDTO criar(ProfessorCreateDTO dto);

	/**
	 * Lista todos os professores cadastrados.
	 * 
	 * @return Lista de DTOs com os dados de todos os professores
	 */
	List<ProfessorResponseDTO> listarTodos();

	/**
	 * Busca um professor específico por ID.
	 * 
	 * @param id ID do professor a ser buscado
	 * @return DTO com os dados do professor
	 * @throws RuntimeException se o professor não for encontrado
	 */
	ProfessorResponseDTO buscarPorId(Long id);

	/**
	 * Atualiza os dados de um professor existente.
	 * 
	 * @param id ID do professor a ser atualizado
	 * @param dto Dados a serem atualizados (nome, telefone, CREFI)
	 * @return DTO com os dados atualizados do professor
	 * @throws RuntimeException se o professor não for encontrado
	 */
	ProfessorResponseDTO atualizar(Long id, ProfessorUpdateDTO dto);

	/**
	 * Remove um professor do sistema.
	 * 
	 * @param id ID do professor a ser removido
	 * @throws RuntimeException se o professor não for encontrado
	 */
	void remover(Long id);

	/**
	 * Lista todos os alunos vinculados ao professor autenticado.
	 * Extrai o email do professor do token JWT e busca seus alunos.
	 * 
	 * @return Lista de DTOs com os alunos do professor
	 */
	List<com.superfit.superfitapp.dto.aluno.AlunoResponseDTO> listarMeusAlunos();
}
