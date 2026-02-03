package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    List<Treino> findByProfessorId(Long professorId);

    List<Treino> findByAlunoId(Long alunoId);

    boolean existsByIdAndProfessorUserEmail(Long id, String email);

    boolean existsByIdAndAlunoUserEmail(Long id, String email);
}
