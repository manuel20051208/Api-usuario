package com.example.apiusuario.Service;

import com.example.apiusuario.Model.Evento;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.EventoRepository;
import com.example.apiusuario.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServicioEvento {

    private final EventoRepository eventoRepository;

    public ServicioEvento(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll();
    }

    public List<Evento> obtenerPorUsuario(Long usuarioId) {
        return eventoRepository.findByUsuarioId(usuarioId);
    }

    public List<Evento> obtenerPorUsuarioYFecha(Long usuarioId, LocalDate fecha) {
        return eventoRepository.findByUsuarioIdAndFecha(usuarioId, fecha);
    }

    public Evento crear(Evento evento, Usuario usuario) {
        evento.setUsuario(usuario);
        return eventoRepository.save(evento);
    }

    public void eliminar(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento no encontrado");
        }
        eventoRepository.deleteById(id);
    }
}
