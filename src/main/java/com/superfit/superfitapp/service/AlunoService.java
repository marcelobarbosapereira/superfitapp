package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.aluno.AlunoCreateDTO;
import com.superfit.superfitapp.dto.aluno.AlunoResponseDTO;
import com.superfit.superfitapp.dto.aluno.AlunoUpdateDTO;

import java.util.List;

/**
 * Interface de serviço para gerenciamento de Alunos.
 * Define operações de CRUD e métodos de segurança para verificação de permissões.
 */
public interface AlunoService {

    /**
     * Verifica se o aluno pertence ao usuário autenticado no token JWT.
     * 
     * @param alunoId ID do aluno a ser verificado
     * @return true se o aluno pertence ao usuário do token, false caso contrário
     */
    boolean isAlunoDoToken(Long alunoId);

    /**
     * Verifica se o aluno está sob supervisão do professor autenticado.
     * 
     * @param alunoId ID do aluno a ser verificado
     * @return true se o aluno pertence ao professor autenticado, false caso contrário
     */
    boolean isAlunoDoProfessor(Long alunoId);

    /**
     * Cria um novo aluno no sistema e o vincula a um professor.
     * 
     * @param dto Dados para criação do aluno (nome, email, telefone, professorId)
     * @return DTO com os dados do aluno criado
     * @throws RuntimeException se o professor não for encontrado
     */
    AlunoResponseDTO criar(AlunoCreateDTO dto);

    /**
     * Lista todos os alunos cadastrados no sistema.
     * 
     * @return Lista de DTOs com os dados de todos os alunos
     */
    List<AlunoResponseDTO> listar();

    /**
     * Busca um aluno específico por seu ID.
     * 
     * @param id ID do aluno a ser buscado
     * @return DTO com os dados do aluno
     * @throws RuntimeException se o aluno não for encontrado
     */
    AlunoResponseDTO buscarPorId(Long id);

    /**
     * Atualiza os dados de um aluno existente.
     * 
     * @param id ID do aluno a ser atualizado
     * @param dto Dados a serem atualizados (nome, telefone, ativo)
     * @return DTO com os dados atualizados do aluno
     * @throws RuntimeException se o aluno não for encontrado
     */
    AlunoResponseDTO atualizar(Long id, AlunoUpdateDTO dto);

    /**
     * Remove um aluno do sistema.
     * 
     * @param id ID do aluno a ser removido
     */
    void remover(Long id);
}