package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.professor.ProfessorCreateDTO;
import com.superfit.superfitapp.dto.professor.ProfessorResponseDTO;
import com.superfit.superfitapp.dto.professor.ProfessorUpdateDTO;
import com.superfit.superfitapp.model.Professor;
import com.superfit.superfitapp.repository.ProfessorRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("professorService")
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorServiceImpl(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    /* =======================
       MÉTODOS DE SEGURANÇA
       ======================= */

    @Override
    public boolean isProfessorDoToken(Long professorId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return professorRepository.existsByIdAndUserEmail(professorId, email);
    }

    /* =======================
       MÉTODOS DE NEGÓCIO
       ======================= */

    @Override
    public ProfessorResponseDTO criar(ProfessorCreateDTO dto) {
        Professor professor = new Professor();
        professor.setNome(dto.getNome());
        professor.setEmail(dto.getEmail());
        professor.setTelefone(dto.getTelefone());
        professor.setCrefi(dto.getCrefi());

        // aqui normalmente você vincula o User com role PROFESSOR
        // professor.setUser(user);

        professor = professorRepository.save(professor);
        return toResponseDTO(professor);
    }

    @Override
    public List<ProfessorResponseDTO> listarTodos() {
        return professorRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProfessorResponseDTO buscarPorId(Long id) {
        Professor professor = professorRepository.findById(
                Objects.requireNonNull(id, "id")
            )
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        return toResponseDTO(professor);
    }

    @Override
    public ProfessorResponseDTO atualizar(Long id, ProfessorUpdateDTO dto) {
        Professor professor = professorRepository.findById(
                Objects.requireNonNull(id, "id")
            )
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        professor.setNome(dto.getNome());
        professor.setTelefone(dto.getTelefone());
        professor.setCrefi(dto.getCrefi());

        professor = professorRepository.save(professor);
        return toResponseDTO(professor);
    }

    @Override
    public void remover(Long id) {
        if (!professorRepository.existsById(Objects.requireNonNull(id, "id"))) {
            throw new RuntimeException("Professor não encontrado");
        }
        professorRepository.deleteById(Objects.requireNonNull(id, "id"));
    }

    /* =======================
       MÉTODO UTILITÁRIO (DTO)
       ======================= */

    private ProfessorResponseDTO toResponseDTO(Professor professor) {
        String email = professor.getUser() != null
                ? professor.getUser().getEmail()
                : professor.getEmail();

        return new ProfessorResponseDTO(
                professor.getId(),
                professor.getNome(),
                email,
                professor.getTelefone(),
                professor.getCrefi()
        );
    }
}

