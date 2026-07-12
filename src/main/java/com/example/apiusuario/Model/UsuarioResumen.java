package com.example.apiusuario.Model;

import jakarta.persistence.*;
import jakarta.validation.groups.Default;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.Immutable;

@Entity(name = "vista_resumen_usuario")
@Data
@Immutable
public class UsuarioResumen {
    @Id
    @Column(name = "usuario_id")
    private Long usuario_id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "tareas_totales")
    private Long tareas_totales;

    @Column(name = "tareas_completadas")
    private Long tareas_completadas;

    @Column(name = "tareas_pendientes")
    private Long tareas_pendientes;

    @Column(name = "progreso_tareas")
    private Double progreso_tareas;

    @Column(name = "promedio_sueno")
    private Double promedio_sueno;

    @Column(name = "calidad_promedio")
    private Double calidad_promedio;
}
