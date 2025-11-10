package com.example.apiusuario.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "Aasjgnlajsgbrh56dsjhasjhk&/%#&%&#%&/&=ajdVv";
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24 horas

    public static String generarToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes())
                .compact();
    }

    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new RuntimeException("Token expirado. Por favor, inicia sesión nuevamente.");
        } catch (Exception e) {
            throw new RuntimeException("Token inválido.");
        }
    }
}