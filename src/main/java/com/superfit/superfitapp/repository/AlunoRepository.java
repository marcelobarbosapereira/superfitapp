package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Aluno> findByAtivo(Boolean ativo);

    List<Aluno> findByNomeContainingIgnoreCase(String nome);
}
