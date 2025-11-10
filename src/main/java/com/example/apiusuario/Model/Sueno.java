package com.example.apiusuario.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sueno")
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

    public Sueno() {
    }

    public Sueno(Usuario usuario, LocalDate fecha, LocalTime horaDormir,
                 LocalTime horaDespertar, double horasDormidas, int calidad) {
        setUsuario(usuario);
        setFecha(fecha);
        setHoraDormir(horaDormir);
        setHoraDespertar(horaDespertar);
        setHorasDormidas(horasDormidas);
        setCalidad(calidad);
    }


    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null){
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
    }

    public LocalTime getHoraDormir() {
        return horaDormir;
    }

    public void setHoraDormir(LocalTime horaDormir) {
        if (horaDormir == null){
            throw new IllegalArgumentException("La hora no puede ser nula");
        }
        this.horaDormir = horaDormir;
    }

    public LocalTime getHoraDespertar() {
        return horaDespertar;
    }

    public void setHoraDespertar(LocalTime horaDespertar) {
        if (horaDespertar == null){
            throw new IllegalArgumentException("La hora no puede ser nula");
        }
        this.horaDespertar = horaDespertar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo");
        }
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nula");
        }
        this.usuario = usuario;
    }

    public double getHorasDormidas() {
        return horasDormidas;
    }

    public void setHorasDormidas(double horasDormidas) {
        if (horasDormidas < 0){
            throw new IllegalArgumentException("La hora no puede ser negativas");
        }
        this.horasDormidas = horasDormidas;
    }

    public int getCalidad() {
        return calidad;
    }

    public void setCalidad(int calidad) {
        this.calidad = calidad;
    }
}
