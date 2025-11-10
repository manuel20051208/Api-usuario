package com.example.apiusuario.Service;

import com.example.apiusuario.Model.Sueno;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.SuenoRepository;
import com.example.apiusuario.Respository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ServicioSueno {
    private final SuenoRepository suenoRepository;
    private final UsuarioRepository usuarioRepository;

    public ServicioSueno(SuenoRepository suenoRepository, UsuarioRepository usuarioRepository) {
        this.suenoRepository = suenoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Sueno registrarSueno(Sueno sueno, Usuario usuario) {
        // Asignar el usuario del token al sueño
        sueno.setUsuario(usuario);

        // Calcular horas dormidas automáticamente si ambas horas están presentes
        if (sueno.getHoraDormir() != null && sueno.getHoraDespertar() != null) {
            Duration duracion = Duration.between(sueno.getHoraDormir(), sueno.getHoraDespertar());
            // Si la hora de despertar es antes de dormir (dormir de noche a mañana)
            if (duracion.isNegative()) {
                duracion = duracion.plusHours(24);
            }
            sueno.setHorasDormidas(duracion.toHours() + duracion.toMinutesPart() / 60.0);
        }

        return suenoRepository.save(sueno);
    }
}
