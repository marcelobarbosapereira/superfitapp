package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.medidas.HistoricoEvolucaoDTO;
import com.superfit.superfitapp.dto.medidas.MedidasCreateDTO;
import com.superfit.superfitapp.dto.medidas.MedidasResponseDTO;
import com.superfit.superfitapp.dto.medidas.MedidasUpdateDTO;

import java.util.List;

/**
 * Interface de serviço para gerenciamento de Medidas corporais de alunos.
 * Define operações de CRUD, histórico de evolução e métodos de segurança.
 */
public interface MedidasService {

    /**
     * Verifica se a medida pertence ao aluno autenticado.
     * 
     * @param medidasId ID da medida a ser verificada
     * @return true se a medida pertence ao aluno autenticado, false caso contrário
     */
    boolean isMedidaDoAluno(Long medidasId);

    /**
     * Verifica se a medida pertence a um aluno supervisionado pelo professor autenticado.
     * 
     * @param medidasId ID da medida a ser verificada
     * @return true se a medida é de um aluno do professor autenticado, false caso contrário
     */
    boolean isMedidaDoProfessor(Long medidasId);

    /**
     * Cria uma nova medição para um aluno.
     * Calcula automaticamente o IMC baseado no peso e altura do aluno.
     * 
     * @param dto Dados da medição (data, peso, peito, cintura, quadril, alunoId)
     * @return DTO com os dados da medida criada incluindo IMC calculado
     * @throws RuntimeException se professor ou aluno não encontrados, ou se aluno não pertence ao professor
     */
    MedidasResponseDTO criar(MedidasCreateDTO dto);

    /**
     * Lista medidas de acordo com o usuário autenticado.
     * Professor: retorna medidas de todos os seus alunos.
     * Aluno: retorna apenas suas próprias medidas.
     * 
     * @return Lista de DTOs com as medidas
     */
    List<MedidasResponseDTO> listarTodas();

    /**
     * Lista todas as medidas de um aluno específico em ordem decrescente de data.
     * 
     * @param alunoId ID do aluno
     * @return Lista de DTOs com as medidas do aluno
     */
    List<MedidasResponseDTO> listarPorAluno(Long alunoId);

    /**
     * Obtém o histórico completo de evolução física de um aluno.
     * Calcula a diferença entre a primeira e última medição (peso e IMC).
     * 
     * @param alunoId ID do aluno
     * @return DTO com histórico de medidas e resumo de evolução
     * @throws RuntimeException se o aluno não for encontrado
     */
    HistoricoEvolucaoDTO obterHistoricoEvolucao(Long alunoId);

    /**
     * Busca uma medida específica por ID.
     * 
     * @param id ID da medida
     * @return DTO com os dados da medida
     * @throws RuntimeException se a medida não for encontrada
     */
    MedidasResponseDTO buscarPorId(Long id);

    /**
     * Atualiza uma medição existente.
     * Recalcula automaticamente o IMC após atualização.
     * 
     * @param id ID da medida a ser atualizada
     * @param dto Novos dados (data, peso, peito, cintura, quadril)
     * @return DTO com os dados atualizados incluindo IMC recalculado
     * @throws RuntimeException se a medida não for encontrada
     */
    MedidasResponseDTO atualizar(Long id, MedidasUpdateDTO dto);

    /**
     * Remove uma medição do sistema.
     * 
     * @param id ID da medida a ser removida
     * @throws RuntimeException se a medida não for encontrada
     */
    void remover(Long id);
}
