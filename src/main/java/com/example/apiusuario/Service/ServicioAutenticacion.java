package com.example.apiusuario.Service;

import com.example.apiusuario.Config.JwtUtil;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class ServicioAutenticacion {
    private static final String BEARER_PREFIX = "Bearer ";

    private final ServicioUsuario servicioUsuario;

    public ServicioAutenticacion(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    public String generarToken(Usuario usuario) {
        return JwtUtil.generarToken(usuario.getEmail());
    }

    public Usuario obtenerUsuarioDesdeBearer(String authorizationHeader) {
        String email = obtenerEmailDesdeBearer(authorizationHeader);
        return servicioUsuario.obtenerUsuarioPorEmail(email);
    }

    public Usuario obtenerUsuarioDesdeCredenciales(String tokenCookie, String authorizationHeader) {
        String token = resolverToken(tokenCookie, authorizationHeader);
        String email = obtenerEmailDesdeToken(token);
        return servicioUsuario.obtenerUsuarioPorEmail(email);
    }

    public String obtenerEmailDesdeBearer(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException("Falta el token");
        }
        return obtenerEmailDesdeToken(authorizationHeader.substring(BEARER_PREFIX.length()));
    }

    private String resolverToken(String tokenCookie, String authorizationHeader) {
        if (tokenCookie != null && !tokenCookie.isBlank()) {
            return tokenCookie;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }

        throw new UnauthorizedException("No autenticado");
    }

    private String obtenerEmailDesdeToken(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("No autenticado");
        }

        try {
            return JwtUtil.parseToken(token).getSubject();
        } catch (RuntimeException exception) {
            throw new UnauthorizedException(exception.getMessage());
        }
    }
}
