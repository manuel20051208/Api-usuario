package com.example.apiusuario.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "vista_resumen_usuario")
public class UsuarioResumen {
    @Id
    private Long usuario_id;
    private String nombre;
    private Long tareas_totales;
    private Long tareas_completadas;
    private Long tareas_pendientes;
    private Double progreso_tareas;
    private Double promedio_sueno;
    private Double calidad_promedio;

    // Getters y setters
    public Long getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(Long usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getTareas_totales() {
        return tareas_totales;
    }

    public void setTareas_totales(Long tareas_totales) {
        this.tareas_totales = tareas_totales;
    }

    public Long getTareas_completadas() {
        return tareas_completadas;
    }

    public void setTareas_completadas(Long tareas_completadas) {
        this.tareas_completadas = tareas_completadas;
    }

    public Long getTareas_pendientes() {
        return tareas_pendientes;
    }

    public void setTareas_pendientes(Long tareas_pendientes) {
        this.tareas_pendientes = tareas_pendientes;
    }

    public Double getProgreso_tareas() {
        return progreso_tareas;
    }

    public void setProgreso_tareas(Double progreso_tareas) {
        this.progreso_tareas = progreso_tareas;
    }

    public Double getPromedio_sueno() {
        return promedio_sueno;
    }

    public void setPromedio_sueno(Double promedio_sueno) {
        this.promedio_sueno = promedio_sueno;
    }

    public Double getCalidad_promedio() {
        return calidad_promedio;
    }

    public void setCalidad_promedio(Double calidad_promedio) {
        this.calidad_promedio = calidad_promedio;
    }
}
