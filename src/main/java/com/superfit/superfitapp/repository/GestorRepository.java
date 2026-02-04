package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Gestor.
 * Fornece operações CRUD e queries customizadas.
 * 
 * Queries customizadas:
 * - findByEmail: Busca gestor por email
 * - existsByEmail: Verifica existência por email
 * - findByAtivo: Filtra gestores por status ativo/inativo
 * - findByDepartamento: Busca gestores por departamento
 */
public interface GestorRepository extends JpaRepository<Gestor, Long> {

    Optional<Gestor> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Gestor> findByAtivo(Boolean ativo);

    List<Gestor> findByDepartamento(String departamento);
}
