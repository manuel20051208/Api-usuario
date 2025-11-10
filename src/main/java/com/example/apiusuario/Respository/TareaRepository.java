package com.example.apiusuario.Respository;
import com.example.apiusuario.Model.Tarea;
import com.example.apiusuario.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    Optional<Object> findByUsuarioAndDescripcion(Usuario usuario, String descripcion);
    List<Tarea> findByUsuario(Usuario usuario);
}
