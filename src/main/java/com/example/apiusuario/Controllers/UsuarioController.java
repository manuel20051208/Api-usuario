package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioUsuario;
import com.example.apiusuario.dto.*;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuario")
@AllArgsConstructor
@Tag(name = "Usuarios", description = "Registro, autenticacion y administracion de usuarios")
public class UsuarioController {

    private final ServicioUsuario servicioUsuario;

    @Operation(summary = "Registrar usuario", description = "Crea una cuenta de usuario nueva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Usuario o email ya registrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/registrar")
    public ResponseEntity<LoginResponse> registrarUsuario(
            @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity.ok(servicioUsuario.registrarUsuario(registerRequest));
    }

    @Operation(summary = "Iniciar sesion", description = "Autentica al usuario y devuelve un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales invalidas",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest login) {
        return servicioUsuario.login(login.email(), login.password());
    }

    @Operation(summary = "Obtener usuario autenticado", description = "Devuelve los datos del usuario asociado al token actual.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario autenticado",
                    content = @Content(schema = @Schema(implementation = UsuarioDataResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<UsuarioDataResponse> me(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        return ResponseEntity.ok(new UsuarioDataResponse(UsuarioResponse.from(usuario)));
    }

    @Operation(summary = "Cerrar sesion", description = "Confirma el cierre de sesion en clientes que manejan JWT de forma stateless.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Sesion cerrada",
            content = @Content(schema = @Schema(implementation = MensajeResponse.class)))
    @PostMapping("/logout")
    public ResponseEntity<MensajeResponse> logout() {
        return ResponseEntity.ok(new MensajeResponse("Desconectado"));
    }

    @Operation(summary = "Buscar usuario por nombre de usuario", description = "Consulta un usuario por su nombre de usuario.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UsuarioDataResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{usuario}")
    public ResponseEntity<UsuarioDataResponse> obtenerUsuarioPorusuario(
            @Parameter(description = "Nombre de usuario", example = "usuario123")
            @PathVariable("usuario") String user) {
        Usuario usuario = servicioUsuario.obtenerUsuarioPorUsuario(user);
        return ResponseEntity.ok(new UsuarioDataResponse(UsuarioResponse.from(usuario)));
    }

    @Operation(summary = "Actualizar datos del usuario", description = "Actualiza los datos del usuario autenticado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado",
                    content = @Content(schema = @Schema(implementation = ActualizacionUsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/actualizarDatos")
    public ResponseEntity<ActualizacionUsuarioResponse> actualizarDatosUsuario(
            @RequestBody Usuario nuevosDatos,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        Usuario actualizado = servicioUsuario.actualizarUsuarioAutenticado(authenticatedUser, nuevosDatos);

        return ResponseEntity.ok(new ActualizacionUsuarioResponse(
                "Actualización de datos correctamente",
                UsuarioResponse.from(actualizado)
        ));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por email.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario eliminado",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/eliminar/{email}")
    public ResponseEntity<MensajeResponse> eliminarUsuario(
            @Parameter(description = "Email del usuario", example = "usuario@correo.com") @PathVariable String email,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Usuario usuario = servicioUsuario.obtenerUsuarioAutenticado(authenticatedUser);
        if (!usuario.getEmail().equals(email)) {
            throw new ForbiddenException("No tienes permiso para eliminar este usuario");
        }

        servicioUsuario.eliminarUsuario(email);
        return ResponseEntity.ok(new MensajeResponse("usuario eliminado"));
    }
}
