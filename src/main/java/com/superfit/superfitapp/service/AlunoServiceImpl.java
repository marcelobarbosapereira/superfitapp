package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.aluno.AlunoCreateDTO;
import com.superfit.superfitapp.dto.aluno.AlunoResponseDTO;
import com.superfit.superfitapp.dto.aluno.AlunoUpdateDTO;
import com.superfit.superfitapp.model.Aluno;
import com.superfit.superfitapp.model.Professor;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    /* =======================
       SEGURANÇA
       ======================= */

    public boolean isAlunoDoToken(Long alunoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return alunoRepository.existsByIdAndUserEmail(alunoId, email);
    }

    public boolean isAlunoDoProfessor(Long alunoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return alunoRepository.existsByIdAndProfessorUserEmail(alunoId, email);
    }

    /* =======================
       NEGÓCIO
       ======================= */

    public AlunoResponseDTO criar(AlunoCreateDTO dto) {
        Professor professor = professorRepository.findById(dto.getProfessorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Aluno aluno = new Aluno();
        aluno.setNome(dto.getNome());
        aluno.setTelefone(dto.getTelefone());
        aluno.setProfessor(professor);
        aluno.setAtivo(true);

        aluno = alunoRepository.save(aluno);
        return toResponseDTO(aluno);
    }

    public List<AlunoResponseDTO> listar() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return toResponseDTO(aluno);
    }

    public AlunoResponseDTO atualizar(Long id, AlunoUpdateDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setNome(dto.getNome());
        aluno.setTelefone(dto.getTelefone());
        aluno.setAtivo(dto.getAtivo());

        aluno = alunoRepository.save(aluno);
        return toResponseDTO(aluno);
    }

    public void remover(Long id) {
        alunoRepository.deleteById(id);
    }

    /* =======================
       DTO
       ======================= */

    private AlunoResponseDTO toResponseDTO(Aluno aluno) {
        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getUser().getEmail(),
                aluno.getTelefone(),
                aluno.getAtivo(),
                aluno.getProfessor().getId(),
                aluno.getProfessor().getNome()
        );
    }
}
