package com.example.apiusuario.Controllers;

import com.example.apiusuario.Config.JwtUtil;
import com.example.apiusuario.Model.Sueno;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.SuenoRepository;
import com.example.apiusuario.Respository.UsuarioRepository;
import com.example.apiusuario.Service.ServicioSueno;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/sueno")
@CrossOrigin(origins = "*")
public class SuenoController {
    private final ServicioSueno servicioSueno;
    private final SuenoRepository suenoRepository;
    private final UsuarioRepository usuarioRepository;

    public SuenoController(ServicioSueno servicioSueno, SuenoRepository suenoRepository,
                           UsuarioRepository usuarioRepository) {
        this.servicioSueno = servicioSueno;
        this.suenoRepository = suenoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Sueno>> listarSueno(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String emailToken = JwtUtil.parseToken(jwt).getSubject();

        Usuario usuario = usuarioRepository.findByEmail(emailToken)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Sueno> lista = suenoRepository.findByUsuario(usuario);
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Sueno> agregarSueno(
            @RequestBody Sueno sueno,
            @RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "");
        String emailToken = JwtUtil.parseToken(jwt).getSubject();

        Usuario usuario = usuarioRepository.findByEmail(emailToken)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Sueno guardado = servicioSueno.registrarSueno(sueno, usuario);
        return ResponseEntity.ok(guardado);
    }


    @GetMapping("/semana")
    public ResponseEntity<List<Map<String, Object>>> getSuenoSemana(
            @RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "");
        String emailToken = JwtUtil.parseToken(jwt).getSubject();

        Usuario usuario = usuarioRepository.findByEmail(emailToken)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);

        List<Map<String, Object>> resumen = suenoRepository
                .findByUsuarioAndFechaBetweenOrderByFechaAsc(usuario, inicioSemana, hoy)
                .stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("fecha", s.getFecha());
                    map.put("horas_dormidas", s.getHorasDormidas());
                    map.put("calidad", s.getCalidad());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(resumen);
    }
}