package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório JPA para a entidade Treino.
 * Fornece operações CRUD e queries customizadas para filtrar treinos.
 * 
 * Queries customizadas:
 * - findByProfessorId: Lista treinos criados por um professor específico
 * - findByAlunoId: Lista treinos atribuídos a um aluno específico
 * - existsByIdAndProfessorUserEmail: Valida se treino pertence ao professor autenticado
 * - existsByIdAndAlunoUserEmail: Valida se treino está atribuído ao aluno autenticado
 */
public interface TreinoRepository extends JpaRepository<Treino, Long> {

    List<Treino> findByProfessorId(Long professorId);

    List<Treino> findByAlunoId(Long alunoId);

    boolean existsByIdAndProfessorUserEmail(Long id, String email);

    boolean existsByIdAndAlunoUserEmail(Long id, String email);
}
