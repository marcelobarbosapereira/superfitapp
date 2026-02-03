package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.medidas.HistoricoEvolucaoDTO;
import com.superfit.superfitapp.dto.medidas.MedidasCreateDTO;
import com.superfit.superfitapp.dto.medidas.MedidasResponseDTO;
import com.superfit.superfitapp.dto.medidas.MedidasUpdateDTO;

import java.util.List;

public interface MedidasService {

    boolean isMedidaDoAluno(Long medidasId);

    boolean isMedidaDoProfessor(Long medidasId);

    MedidasResponseDTO criar(MedidasCreateDTO dto);

    List<MedidasResponseDTO> listarTodas();

    List<MedidasResponseDTO> listarPorAluno(Long alunoId);

    HistoricoEvolucaoDTO obterHistoricoEvolucao(Long alunoId);

    MedidasResponseDTO buscarPorId(Long id);

    MedidasResponseDTO atualizar(Long id, MedidasUpdateDTO dto);

    void remover(Long id);
}
