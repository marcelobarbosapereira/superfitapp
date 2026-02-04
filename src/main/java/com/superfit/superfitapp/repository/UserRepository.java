package com.superfit.superfitapp.repository;

import com.superfit.superfitapp.model.User;
import com.superfit.superfitapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade User.
 * Fornece operações CRUD e queries para gerenciamento de usuários e autenticação.
 * 
 * Queries customizadas:
 * - findByEmail: Busca usuário por email (usado em autenticação e UserDetailsService)
 * - existsByEmail: Verifica existência por email (evita duplicação no cadastro)
 * - findByRole: Lista usuários por role (ADMIN, GESTOR, PROFESSOR, ALUNO)
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}
