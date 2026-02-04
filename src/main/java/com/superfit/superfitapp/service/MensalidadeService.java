package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.mensalidade.MensalidadeCreateDTO;
import com.superfit.superfitapp.dto.mensalidade.MensalidadeResponseDTO;
import com.superfit.superfitapp.dto.mensalidade.MensalidadeUpdateDTO;
import com.superfit.superfitapp.model.StatusMensalidade;

import java.util.List;

/**
 * Interface de serviço para gerenciamento de Mensalidades de alunos.
 * Define operações de CRUD, consultas por status e controle de pagamentos.
 */
public interface MensalidadeService {

    /**
     * Cria uma nova mensalidade para um aluno.
     * Define a data de criação como a data atual.
     * 
     * @param dto Dados da mensalidade (alunoId, valor, status, dataVencimento, mês, ano, observações)
     * @return DTO com os dados da mensalidade criada
     * @throws IllegalArgumentException se o aluno não for encontrado
     */
    MensalidadeResponseDTO criar(MensalidadeCreateDTO dto);

    /**
     * Lista todas as mensalidades cadastradas.
     * 
     * @return Lista de DTOs com todas as mensalidades
     */
    List<MensalidadeResponseDTO> listar();

    /**
     * Lista todas as mensalidades de um aluno específico.
     * Ordena em ordem decrescente de data de vencimento (mais recente primeiro).
     * 
     * @param alunoId ID do aluno
     * @return Lista de DTOs com as mensalidades do aluno
     */
    List<MensalidadeResponseDTO> listarPorAluno(Long alunoId);

    /**
     * Lista mensalidades de um aluno filtrando por status.
     * 
     * @param alunoId ID do aluno
     * @param status Status das mensalidades (PENDENTE, PAGA, ATRASADA)
     * @return Lista de DTOs com as mensalidades que atendem aos critérios
     */
    List<MensalidadeResponseDTO> listarPorAlunoEStatus(Long alunoId, StatusMensalidade status);

    /**
     * Busca uma mensalidade específica por ID.
     * 
     * @param id ID da mensalidade
     * @return DTO com os dados da mensalidade
     * @throws IllegalArgumentException se a mensalidade não for encontrada
     */
    MensalidadeResponseDTO buscarPorId(Long id);

    /**
     * Atualiza os dados de uma mensalidade existente.
     * Se o status for alterado para PAGA e não houver data de pagamento, define como data atual.
     * 
     * @param id ID da mensalidade a ser atualizada
     * @param dto Novos dados (valor, status, dataVencimento, dataPagamento, observações)
     * @return DTO com os dados atualizados
     * @throws IllegalArgumentException se a mensalidade não for encontrada
     */
    MensalidadeResponseDTO atualizar(Long id, MensalidadeUpdateDTO dto);

    /**
     * Remove uma mensalidade do sistema.
     * 
     * @param id ID da mensalidade a ser removida
     * @throws IllegalArgumentException se a mensalidade não for encontrada
     */
    void remover(Long id);

    /**
     * Lista todas as mensalidades com status PENDENTE.
     * 
     * @return Lista de DTOs com mensalidades pendentes
     */
    List<MensalidadeResponseDTO> listarPendentes();

    /**
     * Marca uma mensalidade como paga.
     * Altera o status para PAGA e define a data de pagamento como a data atual.
     * 
     * @param id ID da mensalidade a ser marcada como paga
     * @throws IllegalArgumentException se a mensalidade não for encontrada
     */
    void marcarComoPaga(Long id);

    /**
     * Conta quantas mensalidades pendentes um aluno possui.
     * 
     * @param alunoId ID do aluno
     * @return Número de mensalidades com status PENDENTE
     */
    long countPendentesPorAluno(Long alunoId);
}
