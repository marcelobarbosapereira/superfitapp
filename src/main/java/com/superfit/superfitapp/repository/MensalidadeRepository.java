package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Mensalidade;
import com.superfit.superfitapp.model.StatusMensalidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    List<Mensalidade> findByAlunoId(Long alunoId);

    List<Mensalidade> findByAlunoIdAndStatus(Long alunoId, StatusMensalidade status);

    List<Mensalidade> findByAlunoIdOrderByDataVencimentoDesc(Long alunoId);

    List<Mensalidade> findByStatus(StatusMensalidade status);

    List<Mensalidade> findByDataVencimentoBefore(LocalDate data);

    List<Mensalidade> findByMesReferenciaAndAnoReferencia(String mesReferencia, Integer anoReferencia);

    Optional<Mensalidade> findByAlunoIdAndMesReferenciaAndAnoReferencia(Long alunoId, String mesReferencia, Integer anoReferencia);

    long countByAlunoIdAndStatus(Long alunoId, StatusMensalidade status);
}
