package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Evento;
import com.example.apiusuario.Service.ServicioEvento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.apiusuario.Config.JwtUtil;
import io.jsonwebtoken.Claims;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@CrossOrigin(origins = "*") // tu frontend
public class EventoController {

    private final ServicioEvento servicioEvento;

    public EventoController(ServicioEvento servicioEvento) {
        this.servicioEvento = servicioEvento;
    }

    @GetMapping("/usuario")
    public ResponseEntity<?> obtenerEventosDelUsuario(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 🔒 Validar token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Falta el token");
            }

            String token = authHeader.substring(7);
            Claims claims = JwtUtil.parseToken(token);

            // 🔹 Email desde el token
            String emailUsuario = claims.getSubject();

            // 🔹 Buscar usuario real
            var usuario = servicioEvento.obtenerUsuarioPorEmail(emailUsuario);
            if (usuario == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado para el token");
            }

            List<Evento> eventos = servicioEvento.obtenerPorUsuario(usuario.getId());
            if (eventos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(eventos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener eventos: " + e.getMessage());
        }
    }



    // ✅ Obtener eventos por usuario y fecha específica
    @GetMapping("/usuario/{id}/fecha/{fecha}")
    public ResponseEntity<List<Evento>> obtenerPorUsuarioYFecha(
            @PathVariable Long id,
            @PathVariable String fecha) {
        try {
            LocalDate fechaEvento = LocalDate.parse(fecha);
            List<Evento> eventos = servicioEvento.obtenerPorUsuarioYFecha(id, fechaEvento);
            if (eventos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearEvento(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Evento evento) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Falta el token");
            }

            String token = authHeader.substring(7);
            Claims claims = JwtUtil.parseToken(token);
            String emailUsuario = claims.getSubject();

            var usuario = servicioEvento.obtenerUsuarioPorEmail(emailUsuario);
            if (usuario == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado para el token");
            }

            evento.setUsuario(usuario);

            Evento nuevoEvento = servicioEvento.crear(evento);
            return ResponseEntity.ok(nuevoEvento);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear evento: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        try {
            servicioEvento.eliminar(id);
            return ResponseEntity.ok("Evento eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar el evento: " + e.getMessage());
        }
    }
}
