package com.example.apiusuario.Service;

import com.example.apiusuario.Model.Sueno;
import com.example.apiusuario.Model.Tarea;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.SuenoRepository;
import com.example.apiusuario.Respository.TareaRepository;
import com.example.apiusuario.dto.RegistroSuenoResumenResponse;
import com.example.apiusuario.dto.ResumenUsuarioResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class ServicioResumenUsuario {
    private final SuenoRepository suenoRepository;
    private final TareaRepository tareaRepository;

    public ResumenUsuarioResponse obtenerResumen(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);

        List<Sueno> suenos = suenoRepository.findByUsuarioAndFechaBetweenOrderByFechaAsc(
                usuario, inicioSemana, hoy
        );

        List<RegistroSuenoResumenResponse> registrosSueno = suenos.stream()
                .map(sueno -> new RegistroSuenoResumenResponse(
                        sueno.getFecha(),
                        sueno.getHorasDormidas(),
                        sueno.getCalidad()
                ))
                .toList();

        double promedioSueno = suenos.stream()
                .mapToDouble(Sueno::getHorasDormidas)
                .average()
                .orElse(0);

        double ultimaNoche = suenos.isEmpty() ? 0 : suenos.get(suenos.size() - 1).getHorasDormidas();

        int calidadPromedio = (int) Math.round(
                suenos.stream()
                        .mapToInt(Sueno::getCalidad)
                        .average()
                        .orElse(0)
        );

        String tendenciaTexto = String.format("%+.1fh", ultimaNoche - promedioSueno);

        List<Tarea> tareas = tareaRepository.findByUsuario(usuario);
        long tareasCompletadas = tareas.stream().filter(Tarea::isCompletada).count();
        long tareasPendientes = tareas.size() - tareasCompletadas;
        double progresoTareas = tareas.isEmpty() ? 0 : (tareasCompletadas * 100.0 / tareas.size());

        return new ResumenUsuarioResponse(
                usuario.getNombre(),
                promedioSueno,
                ultimaNoche,
                tendenciaTexto,
                calidadPromedio,
                registrosSueno,
                tareasCompletadas,
                tareasPendientes,
                tareas.size(),
                progresoTareas
        );
    }
}
