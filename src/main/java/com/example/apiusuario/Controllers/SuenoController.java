package com.example.apiusuario.Controllers;

import com.example.apiusuario.Model.Sueno;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Service.ServicioAutenticacion;
import com.example.apiusuario.Service.ServicioSueno;
import com.example.apiusuario.dto.SuenoSemanaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/sueno")
@CrossOrigin(origins = "*")
public class SuenoController {
    private final ServicioSueno servicioSueno;
    private final ServicioAutenticacion servicioAutenticacion;

    public SuenoController(ServicioSueno servicioSueno, ServicioAutenticacion servicioAutenticacion) {
        this.servicioSueno = servicioSueno;
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @GetMapping
    public ResponseEntity<List<Sueno>> listarSueno(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(token);
        List<Sueno> lista = servicioSueno.listarPorUsuario(usuario);
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Sueno> agregarSueno(
            @RequestBody Sueno sueno,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(token);
        Sueno guardado = servicioSueno.registrarSueno(sueno, usuario);
        return ResponseEntity.ok(guardado);
    }

    @GetMapping("/semana")
    public ResponseEntity<List<SuenoSemanaResponse>> getSuenoSemana(
            @RequestHeader(value = "Authorization", required = false) String token) {

        Usuario usuario = servicioAutenticacion.obtenerUsuarioDesdeBearer(token);
        return ResponseEntity.ok(servicioSueno.obtenerResumenSemana(usuario));
    }
}
