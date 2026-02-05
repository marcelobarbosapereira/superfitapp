package com.superfit.superfitapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.superfit.superfitapp.security.JwtAuthenticationFilter;


/**
 * Configuração de segurança do Spring Security.
 * Define políticas de autenticação, autorização e filtros de segurança.
 * 
 * Recursos configurados:
 * - Autenticação baseada em JWT (sem sessões)
 * - Autorização em nível de método (@PreAuthorize)
 * - PasswordEncoder com BCrypt
 * - Rotas públicas e protegidas
 * - Desabilitação de CSRF (API REST stateless)
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Cria o bean AuthenticationManager usado para autenticação.
     * Utilizado pelo AuthService no endpoint de login.
     * 
     * @param configuration Configuração de autenticação do Spring Security
     * @return AuthenticationManager configurado
     * @throws Exception em caso de erro na configuração
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Define o encoder de senhas usado no sistema.
     * BCrypt é um algoritmo de hash adaptativo resistente a ataques de força bruta.
     * 
     * @return BCryptPasswordEncoder para hash de senhas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

        /**
         * Configura a cadeia de filtros de segurança.
         * 
         * Configurações aplicadas:
         * - CSRF desabilitado (API REST stateless)
         * - HTTP Basic desabilitado (usa JWT)
         * - Form Login desabilitado (API REST)
         * - Frame Options desabilitado (permite H2 Console)
         * 
         * Rotas públicas (permitAll):
         * - /auth/** (endpoints de autenticação)
         * - /h2-console/** (console do banco H2)
         * - /home, /logout, / (páginas públicas)
         * 
         * Rotas protegidas (authenticated):
         * - /admin/** (requer autenticação, autorização via @PreAuthorize nos controllers)
         * - /professor/** (requer autenticação)
         * - /aluno/** (requer autenticação)
         * - Qualquer outra rota (requer autenticação)
         * 
         * Filtros:
         * - JwtAuthenticationFilter executado antes de UsernamePasswordAuthenticationFilter
         * 
         * @param http HttpSecurity para configuração
         * @return SecurityFilterChain configurada
         * @throws Exception em caso de erro na configuração
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                .authorizeHttpRequests(auth -> auth
                    // Permitir acesso a recursos estáticos
                    .requestMatchers("/js/**").permitAll()
                    .requestMatchers("/css/**").permitAll()
                    .requestMatchers("/images/**").permitAll()
                    .requestMatchers("/favicon.ico").permitAll()
                    
                    // Endpoints públicos
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/home").permitAll()
                    .requestMatchers("/logout").permitAll()
                    .requestMatchers("/").permitAll()
                    
                    // Endpoints protegidos - requerem autenticação
                    .requestMatchers("/admin/**").authenticated()
                    .requestMatchers("/professor/**").authenticated()
                    .requestMatchers("/aluno/**").authenticated()
                    .requestMatchers("/gestor/**").authenticated()

                    .anyRequest().authenticated()
                );

            // Register JWT filter so Authorization header is processed
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

}
