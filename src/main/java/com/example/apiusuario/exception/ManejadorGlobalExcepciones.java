package com.example.apiusuario.exception;

import com.example.apiusuario.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> manejarNoAutorizado(UnauthorizedException exception) {
        return responder(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> manejarProhibido(ForbiddenException exception) {
        return responder(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> manejarNoEncontrado(ResourceNotFoundException exception) {
        return responder(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> manejarConflicto(ConflictException exception) {
        return responder(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiErrorResponse> manejarSolicitudInvalida(Exception exception) {
        return responder(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> manejarErrorNoControlado(Exception exception) {
        return responder(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor: " + exception.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> responder(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(ApiErrorResponse.of(mensaje));
    }
}
