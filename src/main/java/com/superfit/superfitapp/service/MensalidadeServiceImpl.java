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

@Service
public class MensalidadeServiceImpl implements MensalidadeService {

    @Autowired
    private MensalidadeRepository mensalidadeRepository;

    @Autowired
    private AlunoRepository alunoRepository;

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

    @Override
    public List<MensalidadeResponseDTO> listar() {
        return mensalidadeRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MensalidadeResponseDTO> listarPorAluno(Long alunoId) {
        return mensalidadeRepository.findByAlunoIdOrderByDataVencimentoDesc(alunoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MensalidadeResponseDTO> listarPorAlunoEStatus(Long alunoId, StatusMensalidade status) {
        return mensalidadeRepository.findByAlunoIdAndStatus(alunoId, status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MensalidadeResponseDTO buscarPorId(Long id) {
        Optional<Mensalidade> mensalidade = mensalidadeRepository.findById(id);
        if (mensalidade.isEmpty()) {
            throw new IllegalArgumentException("Mensalidade não encontrada com ID: " + id);
        }
        return toDTO(mensalidade.get());
    }

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

    @Override
    public void remover(Long id) {
        if (!mensalidadeRepository.existsById(id)) {
            throw new IllegalArgumentException("Mensalidade não encontrada com ID: " + id);
        }
        mensalidadeRepository.deleteById(id);
    }

    @Override
    public List<MensalidadeResponseDTO> listarPendentes() {
        return mensalidadeRepository.findByStatus(StatusMensalidade.PENDENTE).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

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

    @Override
    public long countPendentesPorAluno(Long alunoId) {
        return mensalidadeRepository.countByAlunoIdAndStatus(alunoId, StatusMensalidade.PENDENTE);
    }

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
