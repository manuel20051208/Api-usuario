package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Sueno;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioSueno;
import com.example.apiusuario.Service.ServicioUsuario;
import com.example.apiusuario.dto.ApiErrorResponse;
import com.example.apiusuario.dto.SuenoSemanaResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/sueno")
@AllArgsConstructor
@Tag(name = "Sueno", description = "Registro y consulta de horas de sueno")
@SecurityRequirement(name = "bearerAuth")
public class SuenoController {
    private final ServicioSueno servicioSueno;
    private final ServicioUsuario servicioUsuario;

    @Operation(summary = "Listar registros de sueno", description = "Obtiene los registros de sueno del usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de registros"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<Sueno>> listarSueno(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        List<Sueno> lista = servicioSueno.listarPorUsuario(usuario);
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Agregar registro de sueno", description = "Guarda un registro de sueno para el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro creado",
                    content = @Content(schema = @Schema(implementation = Sueno.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/agregar")
    public ResponseEntity<Sueno> agregarSueno(
            @RequestBody Sueno sueno,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        Sueno guardado = servicioSueno.registrarSueno(sueno, usuario);
        return ResponseEntity.ok(guardado);
    }

    @Operation(summary = "Resumen semanal de sueno", description = "Obtiene los registros resumidos de la semana del usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen semanal"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/semana")
    public ResponseEntity<List<SuenoSemanaResponse>> getSuenoSemana(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        return ResponseEntity.ok(servicioSueno.obtenerResumenSemana(usuario));
    }
}
