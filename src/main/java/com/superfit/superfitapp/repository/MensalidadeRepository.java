package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Mensalidade;
import com.superfit.superfitapp.model.StatusMensalidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Mensalidade.
 * Fornece operações CRUD e queries customizadas para consultas de mensalidades.
 * 
 * Queries customizadas:
 * - findByAlunoId: Lista mensalidades de um aluno
 * - findByAlunoIdAndStatus: Filtra mensalidades de um aluno por status (PENDENTE, PAGA, ATRASADA)
 * - findByAlunoIdOrderByDataVencimentoDesc: Lista mensalidades de um aluno ordenadas por vencimento (mais recente primeiro)
 * - findByStatus: Busca mensalidades por status (ex: listar todas pendentes)
 * - findByDataVencimentoBefore: Busca mensalidades vencidas antes de uma data (para detectar atrasos)
 * - findByMesReferenciaAndAnoReferencia: Busca mensalidades de um mês/ano específico
 * - findByAlunoIdAndMesReferenciaAndAnoReferencia: Busca mensalidade específica de um aluno em um mês/ano (evita duplicação)
 * - countByAlunoIdAndStatus: Conta mensalidades de um aluno com determinado status (usado em relatórios)
 */
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
