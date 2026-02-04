package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.mensalidade.MensalidadeCreateDTO;
import com.superfit.superfitapp.dto.mensalidade.MensalidadeResponseDTO;
import com.superfit.superfitapp.dto.mensalidade.MensalidadeUpdateDTO;
import com.superfit.superfitapp.model.Aluno;
import com.superfit.superfitapp.model.Mensalidade;
import com.superfit.superfitapp.model.StatusMensalidade;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.MensalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de gerenciamento de Mensalidades.
 * Gerencia operações CRUD, controle de pagamentos e consultas por status.
 */
@Service
public class MensalidadeServiceImpl implements MensalidadeService {

    @Autowired
    private MensalidadeRepository mensalidadeRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    /**
     * Cria uma nova mensalidade para um aluno.
     * Busca o aluno por ID, define os dados da mensalidade e a data de criação como LocalDate.now().
     * 
     * @param dto Dados da mensalidade (alunoId, valor, status, dataVencimento, mêsReferencia, anoReferencia, observações)
     * @return DTO com a mensalidade criada
     * @throws IllegalArgumentException se o aluno não for encontrado
     */
    @Override
    public MensalidadeResponseDTO criar(MensalidadeCreateDTO dto) {
        Optional<Aluno> aluno = alunoRepository.findById(dto.getAlunoId());
        if (aluno.isEmpty()) {
            throw new IllegalArgumentException("Aluno não encontrado com ID: " + dto.getAlunoId());
        }

        Mensalidade mensalidade = new Mensalidade();
        mensalidade.setAluno(aluno.get());
        mensalidade.setValor(dto.getValor());
        mensalidade.setStatus(dto.getStatus());
        mensalidade.setDataVencimento(dto.getDataVencimento());
        mensalidade.setMesReferencia(dto.getMesReferencia());
        mensalidade.setAnoReferencia(dto.getAnoReferencia());
        mensalidade.setObservacoes(dto.getObservacoes());
        mensalidade.setDataCriacao(LocalDate.now());

        Mensalidade salva = mensalidadeRepository.save(mensalidade);
        return toDTO(salva);
    }

    /**
     * Lista todas as mensalidades cadastradas no sistema.
     * Utiliza stream para converter cada entidade em DTO.
     * 
     * @return Lista de DTOs com todas as mensalidades
     */
    @Override
    public List<MensalidadeResponseDTO> listar() {
        return mensalidadeRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista mensalidades de um aluno específico.
     * Ordena em ordem decrescente de data de vencimento.
     * 
     * @param alunoId ID do aluno
     * @return Lista de DTOs com as mensalidades do aluno
     */
    @Override
    public List<MensalidadeResponseDTO> listarPorAluno(Long alunoId) {
        return mensalidadeRepository.findByAlunoIdOrderByDataVencimentoDesc(alunoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista mensalidades de um aluno filtrando por status específico.
     * 
     * @param alunoId ID do aluno
     * @param status Status desejado (PENDENTE, PAGA, ATRASADA)
     * @return Lista de DTOs com as mensalidades que atendem aos critérios
     */
    @Override
    public List<MensalidadeResponseDTO> listarPorAlunoEStatus(Long alunoId, StatusMensalidade status) {
        return mensalidadeRepository.findByAlunoIdAndStatus(alunoId, status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma mensalidade específica por ID.
     * 
     * @param id ID da mensalidade
     * @return DTO com os dados da mensalidade
     * @throws IllegalArgumentException se a mensalidade não for encontrada
     */
    @Override
    public MensalidadeResponseDTO buscarPorId(Long id) {
        Optional<Mensalidade> mensalidade = mensalidadeRepository.findById(id);
        if (mensalidade.isEmpty()) {
            throw new IllegalArgumentException("Mensalidade não encontrada com ID: " + id);
        }
        return toDTO(mensalidade.get());
    }

    /**
     * Atualiza os dados de uma mensalidade existente.
     * Atualiza apenas os campos fornecidos no DTO (valores não nulos).
     * Se o status for alterado para PAGA e não houver dataPagamento, define como LocalDate.now().
     * 
     * @param id ID da mensalidade a ser atualizada
     * @param dto Novos dados (valor, status, dataVencimento, dataPagamento, observações)
     * @return DTO com os dados atualizados
     * @throws IllegalArgumentException se a mensalidade não for encontrada
     */
    @Override
    public MensalidadeResponseDTO atualizar(Long id, MensalidadeUpdateDTO dto) {
        Optional<Mensalidade> mensalidade = mensalidadeRepository.findById(id);
        if (mensalidade.isEmpty()) {
            throw new IllegalArgumentException("Mensalidade não encontrada com ID: " + id);
        }

        Mensalidade m = mensalidade.get();
        if (dto.getValor() != null) {
            m.setValor(dto.getValor());
        }
        if (dto.getStatus() != null) {
            m.setStatus(dto.getStatus());
            if (dto.getStatus() == StatusMensalidade.PAGA && m.getDataPagamento() == null) {
                m.setDataPagamento(LocalDate.now());
            }
        }
        if (dto.getDataVencimento() != null) {
            m.setDataVencimento(dto.getDataVencimento());
        }
        if (dto.getDataPagamento() != null) {
            m.setDataPagamento(dto.getDataPagamento());
        }
        if (dto.getObservacoes() != null) {
            m.setObservacoes(dto.getObservacoes());
        }

        Mensalidade atualizada = mensalidadeRepository.save(m);
        return toDTO(atualizada);
    }

    /**
     * Remove uma mensalidade do sistema.
     * Verifica existência antes de deletar.
     * 
     * @param id ID da mensalidade a ser removida
     * @throws IllegalArgumentException se a mensalidade não for encontrada
     */
    @Override
    public void remover(Long id) {
        if (!mensalidadeRepository.existsById(id)) {
            throw new IllegalArgumentException("Mensalidade não encontrada com ID: " + id);
        }
        mensalidadeRepository.deleteById(id);
    }

    /**
     * Lista todas as mensalidades com status PENDENTE.
     * 
     * @return Lista de DTOs com mensalidades pendentes de todos os alunos
     */
    @Override
    public List<MensalidadeResponseDTO> listarPendentes() {
        return mensalidadeRepository.findByStatus(StatusMensalidade.PENDENTE).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marca uma mensalidade como paga.
     * Altera o status para PAGA e define a data de pagamento como LocalDate.now().
     * 
     * @param id ID da mensalidade
     * @throws IllegalArgumentException se a mensalidade não for encontrada
     */
    @Override
    public void marcarComoPaga(Long id) {
        Optional<Mensalidade> mensalidade = mensalidadeRepository.findById(id);
        if (mensalidade.isEmpty()) {
            throw new IllegalArgumentException("Mensalidade não encontrada com ID: " + id);
        }

        Mensalidade m = mensalidade.get();
        m.setStatus(StatusMensalidade.PAGA);
        m.setDataPagamento(LocalDate.now());
        mensalidadeRepository.save(m);
    }

    /**
     * Conta o número de mensalidades pendentes de um aluno.
     * 
     * @param alunoId ID do aluno
     * @return Número de mensalidades com status PENDENTE
     */
    @Override
    public long countPendentesPorAluno(Long alunoId) {
        return mensalidadeRepository.countByAlunoIdAndStatus(alunoId, StatusMensalidade.PENDENTE);
    }

    /**
     * Converte a entidade Mensalidade em DTO de resposta.
     * Inclui todos os dados da mensalidade e informações do aluno vinculado.
     * 
     * @param mensalidade Entidade Mensalidade a ser convertida
     * @return DTO com mensalidade e dados do aluno
     */
    private MensalidadeResponseDTO toDTO(Mensalidade mensalidade) {
        return new MensalidadeResponseDTO(
                mensalidade.getId(),
                mensalidade.getAluno().getId(),
                mensalidade.getAluno().getNome(),
                mensalidade.getValor(),
                mensalidade.getStatus(),
                mensalidade.getDataVencimento(),
                mensalidade.getDataPagamento(),
                mensalidade.getMesReferencia(),
                mensalidade.getAnoReferencia(),
                mensalidade.getDataCriacao(),
                mensalidade.getObservacoes()
        );
    }
}
