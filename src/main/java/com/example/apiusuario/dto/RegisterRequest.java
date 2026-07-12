package com.example.apiusuario.dto;

public record RegisterRequest(
        String nombre,
        String usuario,
        String email,
        String password
) {
}
