package com.example.apiusuario.dto;

public record LoginResponse(String mensaje, String token, UsuarioResponse usuario) {
}
