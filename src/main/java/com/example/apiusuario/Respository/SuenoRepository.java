package com.example.apiusuario.Respository;

import com.example.apiusuario.Model.Sueno;
import com.example.apiusuario.Model.Usuario;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface SuenoRepository extends JpaRepositoryImplementation<Sueno, Long> {
    List<Sueno> findByUsuario(Usuario usuario);
    List<Sueno> findByUsuarioAndFechaBetweenOrderByFechaAsc(Usuario usuario, LocalDate inicioSemana, LocalDate hoy);
}
