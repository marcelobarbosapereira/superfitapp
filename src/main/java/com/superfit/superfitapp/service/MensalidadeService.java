package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.mensalidade.MensalidadeCreateDTO;
import com.superfit.superfitapp.dto.mensalidade.MensalidadeResponseDTO;
import com.superfit.superfitapp.dto.mensalidade.MensalidadeUpdateDTO;
import com.superfit.superfitapp.model.StatusMensalidade;

import java.util.List;

public interface MensalidadeService {

    MensalidadeResponseDTO criar(MensalidadeCreateDTO dto);

    List<MensalidadeResponseDTO> listar();

    List<MensalidadeResponseDTO> listarPorAluno(Long alunoId);

    List<MensalidadeResponseDTO> listarPorAlunoEStatus(Long alunoId, StatusMensalidade status);

    MensalidadeResponseDTO buscarPorId(Long id);

    MensalidadeResponseDTO atualizar(Long id, MensalidadeUpdateDTO dto);

    void remover(Long id);

    List<MensalidadeResponseDTO> listarPendentes();

    void marcarComoPaga(Long id);

    long countPendentesPorAluno(Long alunoId);
}
