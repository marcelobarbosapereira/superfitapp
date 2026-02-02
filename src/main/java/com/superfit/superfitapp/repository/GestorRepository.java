package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GestorRepository extends JpaRepository<Gestor, Long> {

    Optional<Gestor> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Gestor> findByAtivo(Boolean ativo);

    List<Gestor> findByDepartamento(String departamento);
}
