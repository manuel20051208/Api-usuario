package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioResumenUsuario;
import com.example.apiusuario.Service.ServicioUsuario;
import com.example.apiusuario.dto.ApiErrorResponse;
import com.example.apiusuario.dto.ResumenUsuarioResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumen")
@Tag(name = "Resumen", description = "Indicadores agregados del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioResumenController {

    private final ServicioResumenUsuario servicioResumenUsuario;
    private final ServicioUsuario servicioUsuario;

    public UsuarioResumenController(ServicioResumenUsuario servicioResumenUsuario,
                                    ServicioUsuario servicioUsuario) {
        this.servicioResumenUsuario = servicioResumenUsuario;
        this.servicioUsuario = servicioUsuario;
    }

    @Operation(summary = "Obtener resumen del usuario", description = "Devuelve metricas de sueno y tareas del usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen obtenido",
                    content = @Content(schema = @Schema(implementation = ResumenUsuarioResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ResumenUsuarioResponse> obtenerResumen(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        return ResponseEntity.ok(servicioResumenUsuario.obtenerResumen(usuario));
    }
}
