package com.example.apiusuario.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "sueno")
@AllArgsConstructor
@NoArgsConstructor
public class Sueno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @Column(name = "hora_dormir")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaDormir;

    @Column(name = "hora_despertar")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaDespertar;

    @Column(name = "horas_dormidas")
    private double horasDormidas;

    @Column(name = "calidad")
    private int calidad;
}
