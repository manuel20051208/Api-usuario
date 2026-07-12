package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Evento;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioEvento;
import com.example.apiusuario.Service.ServicioUsuario;
import com.example.apiusuario.dto.ApiErrorResponse;
import com.example.apiusuario.dto.MensajeResponse;
import com.example.apiusuario.exception.ForbiddenException;
import com.example.apiusuario.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@AllArgsConstructor
@Tag(name = "Eventos", description = "Gestion de eventos del usuario")
@SecurityRequirement(name = "bearerAuth")
public class EventoController {

    private final ServicioEvento servicioEvento;
    private final ServicioUsuario servicioUsuario;

    @Operation(summary = "Listar eventos del usuario autenticado", description = "Obtiene los eventos asociados al token enviado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de eventos"),
            @ApiResponse(responseCode = "204", description = "No hay eventos registrados"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/usuario")
    public ResponseEntity<List<Evento>> obtenerEventosDelUsuario(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        List<Evento> eventos = servicioEvento.obtenerPorUsuario(usuario.getId());

        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Listar eventos por usuario y fecha", description = "Obtiene eventos de un usuario en una fecha especifica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de eventos"),
            @ApiResponse(responseCode = "204", description = "No hay eventos para la fecha"),
            @ApiResponse(responseCode = "400", description = "Fecha o parametros invalidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/usuario/{id}/fecha/{fecha}")
    public ResponseEntity<List<Evento>> obtenerPorUsuarioYFecha(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long id,
            @Parameter(description = "Fecha en formato yyyy-MM-dd", example = "2026-07-11") @PathVariable LocalDate fecha,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        if (!id.equals(usuario.getId())) {
            throw new ForbiddenException("No tienes permiso para consultar eventos de este usuario");
        }

        List<Evento> eventos = servicioEvento.obtenerPorUsuarioYFecha(id, fecha);

        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Crear evento", description = "Crea un evento para el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento creado",
                    content = @Content(schema = @Schema(implementation = Evento.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/crear")
    public ResponseEntity<Evento> crearEvento(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody Evento evento) {

        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        Evento nuevoEvento = servicioEvento.crear(evento, usuario);
        return ResponseEntity.ok(nuevoEvento);
    }

    @Operation(summary = "Eliminar evento", description = "Elimina un evento por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento eliminado",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeResponse> eliminar(
            @Parameter(description = "ID del evento", example = "1") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        servicioEvento.eliminar(id, usuario);
        return ResponseEntity.ok(new MensajeResponse("Evento eliminado correctamente."));
    }
}
