package com.example.apiusuario.Controllers;

import com.example.apiusuario.Config.JwtUtil;
import com.example.apiusuario.Model.Sueno;
import com.example.apiusuario.Model.Tarea;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.SuenoRepository;
import com.example.apiusuario.Respository.TareaRepository;
import com.example.apiusuario.Respository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resumen")
@CrossOrigin(origins = "*")
public class UsuarioResumenController {

    private final UsuarioRepository usuarioRepository;
    private final SuenoRepository suenoRepository;
    private final TareaRepository tareaRepository;

    public UsuarioResumenController(UsuarioRepository usuarioRepository,
                                    SuenoRepository suenoRepository,
                                    TareaRepository tareaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.suenoRepository = suenoRepository;
        this.tareaRepository = tareaRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerResumen(
            @RequestHeader("Authorization") String token) {

        try {
            // 🔹 Extraer usuario desde el token
            String jwt = token.replace("Bearer ", "");
            String email = JwtUtil.parseToken(jwt).getSubject();

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // 🔹 Fechas de la semana actual
            LocalDate hoy = LocalDate.now();
            LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);

            // 🔹 Datos de sueño
            List<Sueno> suenos = suenoRepository.findByUsuarioAndFechaBetweenOrderByFechaAsc(
                    usuario, inicioSemana, hoy
            );

            List<Map<String, Object>> registrosSueno = suenos.stream().map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("fecha", s.getFecha().toString());
                map.put("horas", s.getHorasDormidas());
                map.put("calidad", s.getCalidad());
                return map;
            }).collect(Collectors.toList());

            double promedioSueno = suenos.stream()
                    .mapToDouble(Sueno::getHorasDormidas)
                    .average().orElse(0);

            double ultimaNoche = suenos.isEmpty() ? 0 : suenos.get(suenos.size() - 1).getHorasDormidas();

            int calidadPromedio = (int) Math.round(
                    suenos.stream()
                            .mapToInt(Sueno::getCalidad)
                            .average()
                            .orElse(0)
            );

            String tendenciaTexto = String.format("%+.1fh", ultimaNoche - promedioSueno);

            // 🔹 Datos de tareas
            List<Tarea> tareas = tareaRepository.findByUsuario(usuario);
            long tareasCompletadas = tareas.stream().filter(Tarea::isCompletada).count();
            long tareasPendientes = tareas.size() - tareasCompletadas;
            double progresoTareas = tareas.isEmpty() ? 0 : (tareasCompletadas * 100.0 / tareas.size());

            // 🔹 Construir el resumen final
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("nombre", usuario.getNombre());
            resumen.put("promedio_sueno", promedioSueno);
            resumen.put("ultima_noche", ultimaNoche);
            resumen.put("tendencia_sueno", tendenciaTexto);
            resumen.put("calidad_promedio", calidadPromedio);
            resumen.put("registros_sueno", registrosSueno);

            resumen.put("tareas_completadas", tareasCompletadas);
            resumen.put("tareas_pendientes", tareasPendientes);
            resumen.put("tareas_totales", tareas.size());
            resumen.put("progreso_tareas", progresoTareas);

            return ResponseEntity.ok(resumen);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "No se pudo obtener el resumen: " + e.getMessage());
            return ResponseEntity.status(500).body(Collections.unmodifiableMap(error));
        }
    }
}
