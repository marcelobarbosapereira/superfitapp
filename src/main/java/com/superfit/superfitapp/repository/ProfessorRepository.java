package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Professor.
 * Fornece operações CRUD e queries customizadas.
 * 
 * Queries customizadas:
 * - findByEmail: Busca professor por email
 * - existsByEmail: Verifica existência por email
 * - existsByIdAndUserEmail: Valida se professor pertence ao usuário autenticado (usado em @PreAuthorize)
 */
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdAndUserEmail(Long id, String email);

}
