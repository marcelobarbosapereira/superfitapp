package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.admin.GestorCreateDTO;
import com.superfit.superfitapp.dto.admin.GestorResponseDTO;
import com.superfit.superfitapp.dto.admin.GestorUpdateDTO;
import com.superfit.superfitapp.dto.admin.ChangePasswordDTO;
import com.superfit.superfitapp.dto.admin.ProfessorCreateDTO;
import com.superfit.superfitapp.dto.admin.ProfessorResponseDTO;
import com.superfit.superfitapp.dto.admin.ProfessorUpdateDTO;
import com.superfit.superfitapp.dto.admin.AlunoCreateDTO;
import com.superfit.superfitapp.dto.admin.AlunoResponseDTO;
import com.superfit.superfitapp.dto.admin.AlunoUpdateDTO;
import java.util.List;

public interface AdminService {

    // ===== Gestão de Gestores =====

    GestorResponseDTO cadastrarGestor(GestorCreateDTO dto);

    void excluirGestor(Long gestorId);

    GestorResponseDTO atualizarGestor(Long gestorId, GestorUpdateDTO dto);

    List<GestorResponseDTO> listarGestores();

    // ===== Gestão de Professores =====

    ProfessorResponseDTO cadastrarProfessor(ProfessorCreateDTO dto);

    void excluirProfessor(Long professorId);

    ProfessorResponseDTO atualizarProfessor(Long professorId, ProfessorUpdateDTO dto);

    List<ProfessorResponseDTO> listarProfessores();

    // ===== Gestão de Alunos =====

    AlunoResponseDTO cadastrarAluno(AlunoCreateDTO dto);

    void excluirAluno(Long alunoId);

    AlunoResponseDTO atualizarAluno(Long alunoId, AlunoUpdateDTO dto);

    List<AlunoResponseDTO> listarAlunos();

    // ===== Administração do próprio ADMIN =====

    void alterarSenha(String emailAdmin, ChangePasswordDTO dto);
}
