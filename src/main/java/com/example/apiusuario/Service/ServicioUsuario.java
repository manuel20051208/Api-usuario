package com.example.apiusuario.Service;

import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.TareaRepository;
import com.example.apiusuario.Respository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioUsuario {
    private final UsuarioRepository usuarioRepository;
    private final TareaRepository tareaRepository;

    public ServicioUsuario(UsuarioRepository usuarioRepository, TareaRepository tareaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario login(String email, String password) {

        if (usuarioRepository.findByEmail(email).isEmpty()) {
            throw new IllegalArgumentException("Usuario no existente");
        }

        return usuarioRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("¡Correo y/o usuario incorrecto!"));
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("Usuario no encontrado"));
    }

    public Usuario obtenerUsuarioPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario).orElseThrow(() ->
                new IllegalArgumentException("Usuario no encontrado"));
    }

    public Usuario actualizarUsuarioPorToken(String emailToken, Usuario nuevosDatos) {
        Usuario existente = usuarioRepository.findByEmail(emailToken)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existente"));

        existente.setNombre(nuevosDatos.getNombre());
        existente.setUsuario(nuevosDatos.getUsuario());
        existente.setEmail(nuevosDatos.getEmail());
        return usuarioRepository.save(existente);
    }

    public void eliminarUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("Usuario no existente"));
        usuarioRepository.delete(usuario);
    }
}