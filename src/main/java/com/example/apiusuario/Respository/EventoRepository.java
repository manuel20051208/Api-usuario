package com.example.apiusuario.Respository;

import com.example.apiusuario.Model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByUsuarioId(Long usuarioId);
    List<Evento> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);
}
