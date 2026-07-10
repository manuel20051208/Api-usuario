package com.example.apiusuario.dto;

import java.time.LocalDate;

public record RegistroSuenoResumenResponse(LocalDate fecha, double horas, int calidad) {
}
