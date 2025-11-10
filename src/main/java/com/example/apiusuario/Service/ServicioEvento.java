package com.example.apiusuario.Service;

import com.example.apiusuario.Model.Evento;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.EventoRepository;
import com.example.apiusuario.Respository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ServicioEvento {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    public ServicioEvento(EventoRepository eventoRepository, UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
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

    public Evento crear(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }
}
