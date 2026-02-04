package com.superfit.superfitapp.security;

import com.superfit.superfitapp.model.User;
import com.superfit.superfitapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementação do UserDetailsService do Spring Security.
 * Carrega dados do usuário do banco de dados para autenticação.
 * 
 * Utilizado pelo AuthenticationManager durante o processo de login
 * para validar credenciais e carregar authorities (roles).
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carrega os dados do usuário pelo email (username).
     * 
     * Lógica:
     * 1. Busca o usuário no banco de dados pelo email
     * 2. Lança UsernameNotFoundException se não encontrar
     * 3. Cria um objeto UserDetails do Spring Security com:
     *    - Email como username
     *    - Senha (hash) do banco de dados
     *    - Role convertida em SimpleGrantedAuthority
     * 
     * @param email Email do usuário (usado como username)
     * @return UserDetails com email, senha e authorities
     * @throws UsernameNotFoundException se o usuário não existir
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado: " + email)
                );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
