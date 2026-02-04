package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Despesa;
import com.superfit.superfitapp.model.CategoriaDespesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    List<Despesa> findByDataDespesaBetween(LocalDate inicio, LocalDate fim);

    List<Despesa> findByCategoria(CategoriaDespesa categoria);

    List<Despesa> findByPaga(Boolean paga);

    List<Despesa> findByDataDespesaBetweenAndPaga(LocalDate inicio, LocalDate fim, Boolean paga);

    List<Despesa> findByDataDespesaBetweenOrderByDataDespesaDesc(LocalDate inicio, LocalDate fim);

    @Query("SELECT COALESCE(SUM(d.valor), 0.0) FROM Despesa d WHERE d.dataDespesa BETWEEN :inicio AND :fim")
    Double somarDespesasPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT COALESCE(SUM(d.valor), 0.0) FROM Despesa d WHERE d.dataDespesa BETWEEN :inicio AND :fim AND d.paga = true")
    Double somarDespesasPagasPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT COALESCE(SUM(d.valor), 0.0) FROM Despesa d WHERE d.dataDespesa BETWEEN :inicio AND :fim AND d.paga = false")
    Double somarDespesasPendentesPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT d FROM Despesa d WHERE d.categoria = :categoria AND d.dataDespesa BETWEEN :inicio AND :fim")
    List<Despesa> findByCategoriaPorPeriodo(@Param("categoria") CategoriaDespesa categoria, 
                                            @Param("inicio") LocalDate inicio, 
                                            @Param("fim") LocalDate fim);

    long countByPaga(Boolean paga);
}
