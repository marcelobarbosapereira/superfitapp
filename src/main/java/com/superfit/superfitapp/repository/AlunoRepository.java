package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Aluno.
 * Fornece operações CRUD e queries customizadas para consultas e validações.
 * 
 * Queries customizadas:
 * - findByEmail: Busca aluno por email
 * - existsByEmail: Verifica existência por email
 * - existsByIdAndUserEmail: Valida se aluno pertence ao usuário autenticado
 * - existsByIdAndProfessorUserEmail: Valida se aluno pertence ao professor autenticado
 * - findByAtivo: Filtra alunos por status ativo/inativo
 * - findByNomeContainingIgnoreCase: Busca alunos por parte do nome (case-insensitive)
 */
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdAndUserEmail(Long id, String email);

    boolean existsByIdAndProfessorUserEmail(Long id, String email);

    List<Aluno> findByAtivo(Boolean ativo);

    List<Aluno> findByNomeContainingIgnoreCase(String nome);
}
