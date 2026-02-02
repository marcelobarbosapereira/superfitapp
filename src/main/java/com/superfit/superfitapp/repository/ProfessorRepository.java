package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Professor> findByAtivo(Boolean ativo);

    List<Professor> findByEspecialidade(String especialidade);

    boolean existsByIdAndUserEmail(Long id, String email);

}
