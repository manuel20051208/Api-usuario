package com.example.apiusuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResumenUsuarioResponse(
        String nombre,
        @JsonProperty("promedio_sueno") double promedioSueno,
        @JsonProperty("ultima_noche") double ultimaNoche,
        @JsonProperty("tendencia_sueno") String tendenciaSueno,
        @JsonProperty("calidad_promedio") int calidadPromedio,
        @JsonProperty("registros_sueno") List<RegistroSuenoResumenResponse> registrosSueno,
        @JsonProperty("tareas_completadas") long tareasCompletadas,
        @JsonProperty("tareas_pendientes") long tareasPendientes,
        @JsonProperty("tareas_totales") int tareasTotales,
        @JsonProperty("progreso_tareas") double progresoTareas
) {
}
