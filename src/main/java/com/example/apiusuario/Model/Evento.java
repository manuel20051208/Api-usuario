package com.example.apiusuario.Model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String evento;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    public Evento() {
    }

    public Evento(Long id, Usuario usuario, String evento, String descripcion, LocalDate fecha) {
        setId(id);
        setUsuario(usuario);
        setEvento(evento);
        setDescripcion(descripcion);
        setFecha(fecha);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo.");
        }
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        this.usuario = usuario;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        if (evento == null || evento.isBlank()) {
            throw new IllegalArgumentException("El nombre del evento no puede estar vacío.");
        }
        if (evento.length() < 5 || evento.length() > 100) {
            throw new IllegalArgumentException("El nombre del evento debe tener entre 5 y 100 caracteres.");
        }
        this.evento = evento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        if (descripcion.length() < 5 || descripcion.length() > 255) {
            throw new IllegalArgumentException("La descripción debe tener entre 5 y 255 caracteres.");
        }
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del evento no puede ser nula.");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha del evento no puede ser anterior a hoy.");
        }
        this.fecha = fecha;
    }
}
