package com.cristian.gestor_tareas.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Componente utilitario encargado de realizar operaciones de criptografía y gestión de tokens JSON Web Tokens (JWT).
 * Proporciona métodos aislados para la generación, firmado, extracción de datos y verificación de tokens de acceso.
 * * @author Cristian
 * @version 1.0
 */
@Component
public class JwtUtil {

    /**
     * Genera la firma criptográfica simétrica a partir de la contraseña secreta codificada.
     *
     * @return Un objeto {@link Key} configurado con el algoritmo HMAC de firma.
     */
    private Key getSigningKey() {
        // 32 chars mínimo
        String SECRET = "12345678901234567890123456789012";
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Expide un nuevo token JWT firmado con un periodo de validez estándar de 8 horas.
     *
     * @param username El nombre de usuario que se codificará en el Subject del token.
     * @return Una cadena en formato compacto (String codificado en Base64) que representa el JWT.
     */
    public String generateToken(String username) {
        // 8 horas
        long EXPIRATION = 1000 * 60 * 60 * 8;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (Subject) almacenado dentro de las reclamaciones (Claims) de un token JWT.
     *
     * @param token El token JWT del cual se quiere leer la información.
     * @return El nombre de usuario descifrado en texto plano.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida de forma criptográfica si un token JWT es estructuralmente correcto y no ha expirado.
     *
     * @param token El token JWT a verificar.
     * @param userDetails Los detalles del usuario para contrastar la identidad (reservado para validación extendida).
     * @return true si la firma es válida y el token sigue activo; false en caso de que esté corrupto, manipulado o expirado.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
