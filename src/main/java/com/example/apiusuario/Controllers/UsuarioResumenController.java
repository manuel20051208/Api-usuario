package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioAutenticacion;
import com.example.apiusuario.Service.ServicioResumenUsuario;
import com.example.apiusuario.dto.ResumenUsuarioResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumen")
@CrossOrigin(origins = "*")
public class UsuarioResumenController {

    private final ServicioResumenUsuario servicioResumenUsuario;
    private final ServicioAutenticacion servicioAutenticacion;

    public UsuarioResumenController(ServicioResumenUsuario servicioResumenUsuario,
                                    ServicioAutenticacion servicioAutenticacion) {
        this.servicioResumenUsuario = servicioResumenUsuario;
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @GetMapping
    public ResponseEntity<ResumenUsuarioResponse> obtenerResumen(
            @RequestHeader(value = "Authorization", required = false) String token) {

        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(token);
        return ResponseEntity.ok(servicioResumenUsuario.obtenerResumen(usuario));
    }
}
