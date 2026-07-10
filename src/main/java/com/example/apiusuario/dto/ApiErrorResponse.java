package com.example.apiusuario.dto;

public record ApiErrorResponse(String mensaje, String error) {
    public static ApiErrorResponse of(String mensaje) {
        return new ApiErrorResponse(mensaje, mensaje);
    }
}
