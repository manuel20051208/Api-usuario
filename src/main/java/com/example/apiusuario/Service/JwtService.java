package com.example.apiusuario.Service;

import com.example.apiusuario.Model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private Key getSigningKey() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("Configura jwt.secret.key con una llave Base64 de minimo 32 bytes");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secretKey.trim());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(Usuario usuario) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expirado. Por favor, inicia sesión nuevamente.");
        } catch (Exception e) {
            throw new RuntimeException("Token inválido.");
        }
    }
}
