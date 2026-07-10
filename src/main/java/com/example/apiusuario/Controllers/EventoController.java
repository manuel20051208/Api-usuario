package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Evento;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioAutenticacion;
import com.example.apiusuario.Service.ServicioEvento;
import com.example.apiusuario.dto.MensajeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    private final ServicioEvento servicioEvento;
    private final ServicioAutenticacion servicioAutenticacion;

    public EventoController(ServicioEvento servicioEvento, ServicioAutenticacion servicioAutenticacion) {
        this.servicioEvento = servicioEvento;
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<Evento>> obtenerEventosDelUsuario(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(authHeader);
        List<Evento> eventos = servicioEvento.obtenerPorUsuario(usuario.getId());

        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/usuario/{id}/fecha/{fecha}")
    public ResponseEntity<List<Evento>> obtenerPorUsuarioYFecha(
            @PathVariable Long id,
            @PathVariable LocalDate fecha) {
        List<Evento> eventos = servicioEvento.obtenerPorUsuarioYFecha(id, fecha);

        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(eventos);
    }

    @PostMapping("/crear")
    public ResponseEntity<Evento> crearEvento(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Evento evento) {

        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(authHeader);
        Evento nuevoEvento = servicioEvento.crear(evento, usuario);
        return ResponseEntity.ok(nuevoEvento);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Long id) {
        servicioEvento.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Evento eliminado correctamente."));
    }
}
