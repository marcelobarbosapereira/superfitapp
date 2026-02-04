package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.treino.TreinoCreateDTO;
import com.superfit.superfitapp.dto.treino.TreinoResponseDTO;
import com.superfit.superfitapp.dto.treino.TreinoUpdateDTO;

import java.util.List;

/**
 * Interface de serviço para gerenciamento de Treinos e Exercícios.
 * Define operações de CRUD e métodos de segurança para controle de acesso.
 */
public interface TreinoService {

    /**
     * Verifica se o treino pertence ao professor autenticado.
     * 
     * @param treinoId ID do treino a ser verificado
     * @return true se o treino foi criado pelo professor autenticado, false caso contrário
     */
    boolean isTreinoDoProfessor(Long treinoId);

    /**
     * Verifica se o treino está vinculado ao aluno autenticado.
     * 
     * @param treinoId ID do treino a ser verificado
     * @return true se o treino pertence ao aluno autenticado, false caso contrário
     */
    boolean isTreinoDoAluno(Long treinoId);

    /**
     * Cria um novo treino com exercícios para um aluno.
     * 
     * @param dto Dados do treino (nome, alunoId, lista de exercícios)
     * @return DTO com os dados do treino criado
     * @throws RuntimeException se o aluno não for encontrado ou não pertencer ao professor
     */
    TreinoResponseDTO criar(TreinoCreateDTO dto);

    /**
     * Lista todos os treinos de acordo com o usuário autenticado.
     * Professores veem seus treinos criados, alunos veem seus treinos atribuídos.
     * 
     * @return Lista de DTOs com os treinos
     */
    List<TreinoResponseDTO> listarTodos();

    /**
     * Busca um treino específico por ID.
     * 
     * @param id ID do treino a ser buscado
     * @return DTO com os dados do treino e seus exercícios
     * @throws RuntimeException se o treino não for encontrado
     */
    TreinoResponseDTO buscarPorId(Long id);

    /**
     * Atualiza um treino existente e seus exercícios.
     * Remove todos os exercícios antigos e adiciona os novos.
     * 
     * @param id ID do treino a ser atualizado
     * @param dto Novos dados (nome, lista de exercícios)
     * @return DTO com os dados atualizados
     * @throws RuntimeException se o treino não for encontrado
     */
    TreinoResponseDTO atualizar(Long id, TreinoUpdateDTO dto);

    /**
     * Remove um treino do sistema.
     * Remove também todos os exercícios associados (cascade).
     * 
     * @param id ID do treino a ser removido
     * @throws RuntimeException se o treino não for encontrado
     */
    void remover(Long id);
}
