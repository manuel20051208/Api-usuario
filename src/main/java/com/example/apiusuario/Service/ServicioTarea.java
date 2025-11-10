package com.example.apiusuario.Service;

import com.example.apiusuario.Model.Tarea;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.TareaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioTarea {
    private final TareaRepository tareaRepository;

    public ServicioTarea(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    public Tarea agregarTarea(Tarea tarea) {
        boolean existe = tareaRepository
                .findByUsuarioAndDescripcion(tarea.getUsuario(), tarea.getDescripcion())
                .isPresent();

        if (existe) {
            throw new IllegalArgumentException("Ya tienes esta tarea en tu lista.");
        }

        return tareaRepository.save(tarea);
    }

    public List<Tarea> listarTareasPorUsuario(Usuario usuario) {
        return tareaRepository.findByUsuario(usuario);
    }

    public void eliminarTarea(Long id, Usuario usuario) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // comparar por id para evitar problemas con equals mal implementados
        if (tarea.getUsuario() == null || tarea.getUsuario().getId() == null ||
                !tarea.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar esta tarea");
        }

        tareaRepository.delete(tarea);
    }

    public Tarea toggleCompletado(Long id, Usuario usuario) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Validar que la tarea pertenece al usuario
        if (!tarea.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para actualizar esta tarea");
        }

        // Cambiar el estado de completado
        tarea.setCompletada(!tarea.isCompletada());
        return tareaRepository.save(tarea);
    }

}
