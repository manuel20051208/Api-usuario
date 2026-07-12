package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Tarea;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioTarea;
import com.example.apiusuario.Service.ServicioUsuario;
import com.example.apiusuario.dto.ApiErrorResponse;
import com.example.apiusuario.dto.MensajeResponse;
import com.example.apiusuario.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tarea")
@CrossOrigin(origins = "*")
@Tag(name = "Tareas", description = "Gestion de tareas del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class TareaController {

    private final ServicioTarea servicioTarea;
    private final ServicioUsuario servicioUsuario;

    public TareaController(ServicioTarea servicioTarea, ServicioUsuario servicioUsuario) {
        this.servicioTarea = servicioTarea;
        this.servicioUsuario = servicioUsuario;
    }

    @Operation(summary = "Agregar tarea", description = "Crea una tarea para el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea creada",
                    content = @Content(schema = @Schema(implementation = Tarea.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/agregar")
    public ResponseEntity<Tarea> agregarTarea(
            @RequestBody Tarea tarea,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        Tarea nuevaTarea = servicioTarea.agregarTarea(tarea, usuario);
        return ResponseEntity.ok(nuevaTarea);
    }

    @Operation(summary = "Listar tareas", description = "Obtiene todas las tareas del usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de tareas"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<Tarea>> listarTareas(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        List<Tarea> tareas = servicioTarea.listarTareasPorUsuario(usuario);
        return ResponseEntity.ok(tareas);
    }

    @Operation(summary = "Eliminar tarea", description = "Elimina una tarea propia por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea eliminada",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "La tarea no pertenece al usuario",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeResponse> eliminarTarea(
            @Parameter(description = "ID de la tarea", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        servicioTarea.eliminarTarea(id, usuario);
        return ResponseEntity.ok(new MensajeResponse("Tarea eliminada correctamente"));
    }

    @Operation(summary = "Cambiar estado de tarea", description = "Alterna una tarea entre completada y pendiente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea actualizada",
                    content = @Content(schema = @Schema(implementation = Tarea.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "La tarea no pertenece al usuario",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/completar/{id}")
    public ResponseEntity<Tarea> toggleCompletado(
            @Parameter(description = "ID de la tarea", example = "1") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        Tarea actualizado = servicioTarea.toggleCompletado(id, usuario);
        return ResponseEntity.ok(actualizado);
    }
}
