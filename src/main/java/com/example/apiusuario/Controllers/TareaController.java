package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Tarea;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioAutenticacion;
import com.example.apiusuario.Service.ServicioTarea;
import com.example.apiusuario.dto.MensajeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tarea")
@CrossOrigin(origins = "*")
public class TareaController {

    private final ServicioTarea servicioTarea;
    private final ServicioAutenticacion servicioAutenticacion;

    public TareaController(ServicioTarea servicioTarea, ServicioAutenticacion servicioAutenticacion) {
        this.servicioTarea = servicioTarea;
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @PostMapping("/agregar")
    public ResponseEntity<Tarea> agregarTarea(
            @RequestBody Tarea tarea,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(token);
        Tarea nuevaTarea = servicioTarea.agregarTarea(tarea, usuario);
        return ResponseEntity.ok(nuevaTarea);
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> listarTareas(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(token);
        List<Tarea> tareas = servicioTarea.listarTareasPorUsuario(usuario);
        return ResponseEntity.ok(tareas);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeResponse> eliminarTarea(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(token);
        servicioTarea.eliminarTarea(id, usuario);
        return ResponseEntity.ok(new MensajeResponse("Tarea eliminada correctamente"));
    }

    @PutMapping("/completar/{id}")
    public ResponseEntity<Tarea> toggleCompletado(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(token);
        Tarea actualizado = servicioTarea.toggleCompletado(id, usuario);
        return ResponseEntity.ok(actualizado);
    }
}
