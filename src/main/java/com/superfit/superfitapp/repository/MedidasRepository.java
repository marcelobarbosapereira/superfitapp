package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Medidas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedidasRepository extends JpaRepository<Medidas, Long> {

    List<Medidas> findByAlunoIdOrderByDataDesc(Long alunoId);

    List<Medidas> findByAlunoIdOrderByDataAsc(Long alunoId);

    @Query("SELECT m FROM Medidas m WHERE m.aluno.professor.user.email = :email ORDER BY m.data DESC")
    List<Medidas> findByProfessorEmail(@Param("email") String email);

    boolean existsByIdAndAlunoUserEmail(Long id, String email);

    boolean existsByIdAndAlunoProfessorUserEmail(Long id, String email);
}
