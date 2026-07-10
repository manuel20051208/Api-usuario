package com.example.apiusuario.Service;

import com.example.apiusuario.dto.SuenoSemanaResponse;
import com.example.apiusuario.Model.Sueno;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.SuenoRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class ServicioSueno {
    private final SuenoRepository suenoRepository;

    public ServicioSueno(SuenoRepository suenoRepository) {
        this.suenoRepository = suenoRepository;
    }

    public List<Sueno> listarPorUsuario(Usuario usuario) {
        return suenoRepository.findByUsuario(usuario);
    }

    public Sueno registrarSueno(Sueno sueno, Usuario usuario) {
        sueno.setUsuario(usuario);

        if (sueno.getHoraDormir() != null && sueno.getHoraDespertar() != null) {
            Duration duracion = Duration.between(sueno.getHoraDormir(), sueno.getHoraDespertar());
            if (duracion.isNegative()) {
                duracion = duracion.plusHours(24);
            }
            sueno.setHorasDormidas(duracion.toHours() + duracion.toMinutesPart() / 60.0);
        }

        return suenoRepository.save(sueno);
    }

    public List<SuenoSemanaResponse> obtenerResumenSemana(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);

        return suenoRepository
                .findByUsuarioAndFechaBetweenOrderByFechaAsc(usuario, inicioSemana, hoy)
                .stream()
                .map(sueno -> new SuenoSemanaResponse(
                        sueno.getFecha(),
                        sueno.getHorasDormidas(),
                        sueno.getCalidad()
                ))
                .toList();
    }
}
