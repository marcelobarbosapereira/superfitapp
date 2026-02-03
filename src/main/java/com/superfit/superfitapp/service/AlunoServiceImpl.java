package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.aluno.AlunoCreateDTO;
import com.superfit.superfitapp.dto.aluno.AlunoResponseDTO;
import com.superfit.superfitapp.dto.aluno.AlunoUpdateDTO;
import com.superfit.superfitapp.model.Aluno;
import com.superfit.superfitapp.model.Professor;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.ProfessorRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("alunoService")
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public AlunoServiceImpl(AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    /* =======================
       SEGURANÇA
       ======================= */

    @Override
    public boolean isAlunoDoToken(Long alunoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return alunoRepository.existsByIdAndUserEmail(alunoId, email);
    }

    @Override
    public boolean isAlunoDoProfessor(Long alunoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return alunoRepository.existsByIdAndProfessorUserEmail(alunoId, email);
    }

    /* =======================
       NEGÓCIO
       ======================= */

    @Override
    public AlunoResponseDTO criar(AlunoCreateDTO dto) {
        Professor professor = professorRepository.findById(
                Objects.requireNonNull(dto.getProfessorId(), "professorId")
            )
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Aluno aluno = new Aluno();
        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setProfessor(professor);
        aluno.setAtivo(true);

        aluno = alunoRepository.save(aluno);
        return toResponseDTO(aluno);
    }

    @Override
    public List<AlunoResponseDTO> listar() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(
                Objects.requireNonNull(id, "id")
            )
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return toResponseDTO(aluno);
    }

    @Override
    public AlunoResponseDTO atualizar(Long id, AlunoUpdateDTO dto) {
        Aluno aluno = alunoRepository.findById(
                Objects.requireNonNull(id, "id")
            )
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setNome(dto.getNome());
        aluno.setTelefone(dto.getTelefone());
        aluno.setAtivo(dto.getAtivo());

        aluno = alunoRepository.save(aluno);
        return toResponseDTO(aluno);
    }

    @Override
    public void remover(Long id) {
        alunoRepository.deleteById(Objects.requireNonNull(id, "id"));
    }

    /* =======================
       DTO
       ======================= */

    private AlunoResponseDTO toResponseDTO(Aluno aluno) {
        String email = aluno.getUser() != null
            ? aluno.getUser().getEmail()
            : aluno.getEmail();

        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
            email,
                aluno.getTelefone(),
                aluno.getAtivo(),
                aluno.getProfessor().getId(),
                aluno.getProfessor().getNome()
        );
    }
}
