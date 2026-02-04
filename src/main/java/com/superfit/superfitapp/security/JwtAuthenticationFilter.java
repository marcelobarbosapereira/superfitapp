package com.superfit.superfitapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.List;
import java.io.IOException;

/**
 * Filtro de autenticação JWT para Spring Security.
 * Intercepta todas as requisições HTTP (exceto rotas públicas) e valida tokens JWT.
 * 
 * Lógica de execução:
 * 1. Extrai token do header Authorization (Bearer) ou do cookie "jwtToken"
 * 2. Valida o token usando JwtService
 * 3. Extrai email e role do token
 * 4. Cria um UsernamePasswordAuthenticationToken com as authorities
 * 5. Define o token no SecurityContextHolder para uso nos controllers
 * 
 * Rotas que pulam o filtro (shouldNotFilter):
 * - /auth/* (endpoints de autenticação)
 * - /h2-console (banco de dados H2)
 * - /, /home (páginas públicas)
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    /**
     * Define quais requisições devem pular este filtro.
     * Rotas públicas e de autenticação não precisam de validação JWT.
     * 
     * @param request Requisição HTTP
     * @return true para pular o filtro, false para executar
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip JWT filter for auth endpoints and public endpoints
        return path.startsWith("/auth/")
            || path.startsWith("/h2-console")
            || path.equals("/")
            || path.equals("/home");
    }

    /**
     * Executa a lógica de validação JWT em cada requisição.
     * 
     * Fluxo de execução:
     * 1. Tenta extrair token do header Authorization (formato: "Bearer <token>")
     * 2. Se não encontrar, tenta extrair do cookie "jwtToken"
     * 3. Valida o token usando JwtService.isTokenValid()
     * 4. Extrai email (subject) e role (claim) do token
     * 5. Verifica se já existe autenticação no SecurityContext
     * 6. Cria SimpleGrantedAuthority com a role
     * 7. Cria UsernamePasswordAuthenticationToken e define no SecurityContext
     * 8. Adiciona detalhes da requisição ao authentication
     * 9. Continua o filtro chain
     * 
     * Em caso de erro, loga a mensagem e continua sem autenticar.
     * 
     * @param request Requisição HTTP
     * @param response Resposta HTTP
     * @param filterChain Cadeia de filtros
     * @throws ServletException em caso de erro no filtro
     * @throws IOException em caso de erro de I/O
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

    try {
        String token = null;

        // Tentar obter o token do header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } 
        // Se não encontrar no header, tentar obter do cookie
        else if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && !token.isEmpty() && jwtService.isTokenValid(token)) {
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            if (email != null && role != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(authority)
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    } catch (Exception e) {
        System.err.println("Erro ao processar JWT: " + e.getMessage());
        e.printStackTrace();
    }

        filterChain.doFilter(request, response);
    }

}
