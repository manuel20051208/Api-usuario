package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioAutenticacion;
import com.example.apiusuario.Service.ServicioUsuario;
import com.example.apiusuario.dto.ActualizacionUsuarioResponse;
import com.example.apiusuario.dto.LoginResponse;
import com.example.apiusuario.dto.MensajeResponse;
import com.example.apiusuario.dto.UsuarioDataResponse;
import com.example.apiusuario.dto.UsuarioResponse;
import com.example.apiusuario.security.AuthCookieFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final ServicioUsuario servicioUsuario;
    private final ServicioAutenticacion servicioAutenticacion;
    private final AuthCookieFactory authCookieFactory;

    public UsuarioController(ServicioUsuario servicioUsuario,
                             ServicioAutenticacion servicioAutenticacion,
                             AuthCookieFactory authCookieFactory) {
        this.servicioUsuario = servicioUsuario;
        this.servicioAutenticacion = servicioAutenticacion;
        this.authCookieFactory = authCookieFactory;
    }

    @PostMapping("/registrar")
    public ResponseEntity<MensajeResponse> registrarUsuario(@RequestBody Usuario usuario) {
        servicioUsuario.registrarUsuario(usuario);
        return ResponseEntity.ok(new MensajeResponse("Usuario registrado correctamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Usuario login, HttpServletResponse response) {
        Usuario usuario = servicioUsuario.login(login.getEmail(), login.getPassword());
        String token = servicioAutenticacion.generarToken(usuario);

        response.addHeader(HttpHeaders.SET_COOKIE, authCookieFactory.crearCookieSesion(token).toString());

        return ResponseEntity.ok(new LoginResponse(
                "Login exitoso. Bienvenido: " + usuario.getNombre(),
                token,
                UsuarioResponse.from(usuario)
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDataResponse> me(
            @CookieValue(value = "token", required = false) String tokenCookie,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeCredenciales(tokenCookie, authHeader);
        return ResponseEntity.ok(new UsuarioDataResponse(UsuarioResponse.from(usuario)));
    }

    @PostMapping("/logout")
    public ResponseEntity<MensajeResponse> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, authCookieFactory.crearCookieLogout().toString());
        return ResponseEntity.ok(new MensajeResponse("Desconectado"));
    }

    @GetMapping("/{usuario}")
    public ResponseEntity<UsuarioDataResponse> obtenerUsuarioPorusuario(@PathVariable("usuario") String user) {
        Usuario usuario = servicioUsuario.obtenerUsuarioPorUsuario(user);
        return ResponseEntity.ok(new UsuarioDataResponse(UsuarioResponse.from(usuario)));
    }

    @PutMapping("/actualizarDatos")
    public ResponseEntity<ActualizacionUsuarioResponse> actualizarDatosUsuario(
            @RequestBody Usuario nuevosDatos,
            @RequestHeader(value = "Authorization", required = false) String token) {

        String emailToken = servicioAutenticacion.obtenerEmailDesdeBearer(token);
        Usuario actualizado = servicioUsuario.actualizarUsuarioPorToken(emailToken, nuevosDatos);

        return ResponseEntity.ok(new ActualizacionUsuarioResponse(
                "Actualización de datos correctamente",
                UsuarioResponse.from(actualizado)
        ));
    }

    @DeleteMapping("/eliminar/{email}")
    public ResponseEntity<MensajeResponse> eliminarUsuario(@PathVariable String email) {
        servicioUsuario.eliminarUsuario(email);
        return ResponseEntity.ok(new MensajeResponse("usuario eliminado"));
    }
}
