package com.example.apiusuario.Controllers;

import com.example.apiusuario.Config.JwtUtil;
import com.example.apiusuario.Model.Tarea;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioTarea;
import com.example.apiusuario.Service.ServicioUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarea")
@CrossOrigin(origins = "*")
public class TareaController {

    private final ServicioTarea servicioTarea;
    private final ServicioUsuario servicioUsuario;

    public TareaController(ServicioTarea servicioTarea, ServicioUsuario servicioUsuario) {
        this.servicioTarea = servicioTarea;
        this.servicioUsuario = servicioUsuario;
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarTarea(
            @RequestBody Tarea tarea,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String emailToken = JwtUtil.parseToken(jwt).getSubject();

            Usuario usuario = servicioUsuario.obtenerUsuarioPorEmail(emailToken);

            tarea.setUsuario(usuario);
            Tarea nuevaTarea = servicioTarea.agregarTarea(tarea);
            return ResponseEntity.ok(nuevaTarea);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> listarTareas(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String email = JwtUtil.parseToken(jwt).getSubject();

        Usuario usuario = servicioUsuario.obtenerUsuarioPorEmail(email);
        List<Tarea> tareas = servicioTarea.listarTareasPorUsuario(usuario);
        return ResponseEntity.ok(tareas);
    }

    // --- DELETE corregido: path /eliminar/{id} y usa token para obtener usuario ---
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarTarea(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = JwtUtil.parseToken(jwt).getSubject();

            Usuario usuario = servicioUsuario.obtenerUsuarioPorEmail(email);

            servicioTarea.eliminarTarea(id, usuario);
            return ResponseEntity.ok(Map.of("mensaje", "Tarea eliminada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- PUT para toggle completado ---
    @PutMapping("/completar/{id}")
    public ResponseEntity<?> toggleCompletado(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String emailToken = JwtUtil.parseToken(jwt).getSubject();

            Usuario usuario = servicioUsuario.obtenerUsuarioPorEmail(emailToken);

            Tarea actualizado = servicioTarea.toggleCompletado(id, usuario);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        }
    }
}