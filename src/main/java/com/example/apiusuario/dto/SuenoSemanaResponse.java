package com.example.apiusuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record SuenoSemanaResponse(
        LocalDate fecha,
        @JsonProperty("horas_dormidas") double horasDormidas,
        int calidad
) {
}
