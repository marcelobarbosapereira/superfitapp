package com.superfit.superfitapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import com.superfit.superfitapp.model.Role;

/**
 * Serviço para geração, validação e extração de dados de tokens JWT.
 * Utiliza a biblioteca jjwt (io.jsonwebtoken) para criar e parsear tokens.
 * 
 * Configurações:
 * - Algoritmo: HS256 (HMAC com SHA-256)
 * - Expiração: 24 horas
 * - Claims armazenadas: email (subject) e role
 */
@Service
public class JwtService {

    private static final String SECRET_KEY = "superfitapp-secret-key-super-segura-2026";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24h

    /**
     * Obtém a chave de assinatura HMAC a partir da SECRET_KEY.
     * Converte a string secret em bytes e gera uma Key adequada para HS256.
     * 
     * @return Chave de assinatura para JWT
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Gera um token JWT para um usuário.
     * 
     * Lógica:
     * - Define o subject como o email do usuário
     * - Adiciona claim "role" com o nome da role
     * - Define issuedAt como data/hora atual
     * - Define expiração para 24 horas a partir de agora
     * - Assina com HS256 usando a chave secreta
     * 
     * @param email Email do usuário
     * @param role Role do usuário (ADMIN, GESTOR, PROFESSOR, ALUNO)
     * @return Token JWT assinado
     */
    public String generateToken(String email, Role role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai a role armazenada no token.
     * Lê o claim "role" do payload do JWT.
     * 
     * @param token Token JWT válido
     * @return Nome da role como String
     */
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Extrai o email (subject) armazenado no token.
     * Lê o campo subject do payload do JWT.
     * 
     * @param token Token JWT válido
     * @return Email do usuário
     */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Valida se um token é válido.
     * Tenta parsear o token e verificar assinatura e expiração.
     * Retorna false se o token estiver expirado, com assinatura inválida ou malformado.
     * 
     * @param token Token JWT a ser validado
     * @return true se o token é válido, false caso contrário
     */
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parseia o token e retorna os claims (dados do payload).
     * Verifica a assinatura usando a chave secreta.
     * Lança exceção se o token for inválido ou expirado.
     * 
     * @param token Token JWT
     * @return Claims do token (subject, role, issuedAt, expiration)
     * @throws io.jsonwebtoken.JwtException se o token for inválido
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
