package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdAndUserEmail(Long id, String email);

}
