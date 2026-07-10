package com.example.apiusuario.dto;

import com.example.apiusuario.Model.Usuario;

public record UsuarioResponse(Long id, String usuario, String nombre, String email) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getEmail()
        );
    }
}
