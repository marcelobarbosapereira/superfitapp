package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Medidas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositório JPA para a entidade Medidas.
 * Fornece operações CRUD e queries customizadas com ordenação e filtros.
 * 
 * Queries customizadas:
 * - findByAlunoIdOrderByDataDesc: Lista medidas de um aluno ordenadas por data (mais recente primeiro)
 * - findByAlunoIdOrderByDataAsc: Lista medidas de um aluno ordenadas por data (mais antiga primeiro) - usado em histórico de evolução
 * - findByProfessorEmail: JPQL query que busca medidas de todos os alunos de um professor específico
 * - existsByIdAndAlunoUserEmail: Valida se medida pertence ao aluno autenticado
 * - existsByIdAndAlunoProfessorUserEmail: Valida se medida é de um aluno do professor autenticado
 */
public interface MedidasRepository extends JpaRepository<Medidas, Long> {

    List<Medidas> findByAlunoIdOrderByDataDesc(Long alunoId);

    List<Medidas> findByAlunoIdOrderByDataAsc(Long alunoId);

    @Query("SELECT m FROM Medidas m WHERE m.aluno.professor.user.email = :email ORDER BY m.data DESC")
    List<Medidas> findByProfessorEmail(@Param("email") String email);

    boolean existsByIdAndAlunoUserEmail(Long id, String email);

    boolean existsByIdAndAlunoProfessorUserEmail(Long id, String email);
}
