package com.example.apiusuario.Model;

import com.example.apiusuario.Model.Usuario;
import jakarta.persistence.*;

@Entity
@Table(name = "tarea")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String descripcion;

    private boolean completada;


    public Tarea() {
    }

    public Tarea(Long id, Usuario usuario, String descripcion, boolean completada) {
        setId(id);
        setUsuario(usuario);
        setDescripcion(descripcion);
        setCompletada(completada);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
