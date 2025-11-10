package com.example.apiusuario.Controllers;

import com.example.apiusuario.Config.JwtUtil;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioUsuario;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final ServicioUsuario servicioUsuario;

    public UsuarioController(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, String>> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            servicioUsuario.registrarUsuario(usuario);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Usuario login, HttpServletResponse response) {
        try {
            Usuario usuario = servicioUsuario.login(login.getEmail(), login.getPassword());

            String token = JwtUtil.generarToken(usuario.getEmail());
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false)         // en dev local: false. En producción: true (con HTTPS)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Lax")       // Lax suele funcionar en este flujo local
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());


            Map<String, Object> responseBody = Map.of(
                    "mensaje", "Login exitoso. Bienvenido: " + usuario.getNombre(),
                    "token", token,
                    "usuario", Map.of(
                            "id", usuario.getId(),
                            "usuario", usuario.getUsuario(),
                            "nombre", usuario.getNombre(),
                            "email", usuario.getEmail()
                    )
            );
            return ResponseEntity.ok(responseBody);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = Map.of("mensaje", "Login fallido: " + e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @CookieValue(value = "token", required = false) String tokenCookie,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = tokenCookie;
        if ((token == null || token.isBlank()) && authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "No autenticado"));
        }

        try {
            var claims = JwtUtil.parseToken(token);
            String email = claims.getSubject();
            Usuario usuario = servicioUsuario.obtenerUsuarioPorEmail(email);

            if (usuario == null) {
                return ResponseEntity.status(404).body(Map.of("mensaje", "Usuario no encontrado"));
            }

            Map<String, Object> usuarioSeguro = Map.of(
                    "id", usuario.getId(),
                    "usuario", usuario.getUsuario(),
                    "nombre", usuario.getNombre(),
                    "email", usuario.getEmail()
            );

            return ResponseEntity.ok(Map.of("usuario", usuarioSeguro));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "Token inválido"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .domain("localhost")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(Map.of("mensaje", "Desconectado"));
    }

    @GetMapping("/{usuario}")
    public ResponseEntity<Map<String, Object>> obtenerUsuarioPorusuario(@PathVariable("usuario") String user) {
        try {
            Usuario usuario = servicioUsuario.obtenerUsuarioPorUsuario(user);
            return ResponseEntity.ok(Map.of("usuario", usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/actualizarDatos")
    public ResponseEntity<Map<String, Object>> actualizarDatosUsuario(
            @RequestBody Usuario nuevosDatos,
            @RequestHeader("Authorization") String token) {

        try {
            String jwt = token.replace("Bearer ", "");
            String emailToken = JwtUtil.parseToken(jwt).getSubject();

            Usuario actualizado = servicioUsuario.actualizarUsuarioPorToken(emailToken, nuevosDatos);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Actualización de datos correctamente",
                    "usuario", actualizado
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "Token inválido o expirado"));
        }
    }

    @DeleteMapping("/eliminar/{email}")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(@PathVariable String email){
        try{
            servicioUsuario.eliminarUsuario(email);
            return ResponseEntity.ok(Map.of("mensaje", "usuario eliminado"));
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }
}