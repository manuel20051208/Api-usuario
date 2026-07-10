package com.example.apiusuario.Service;

import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.UsuarioRepository;
import com.example.apiusuario.exception.ConflictException;
import com.example.apiusuario.exception.ResourceNotFoundException;
import com.example.apiusuario.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioUsuario {
    private final UsuarioRepository usuarioRepository;

    public ServicioUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
            throw new UnauthorizedException("Usuario no existente");
        }

        return usuarioRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new UnauthorizedException("¡Correo y/o usuario incorrecto!"));
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Usuario obtenerUsuarioPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario).orElseThrow(() ->
                new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Usuario actualizarUsuarioPorToken(String emailToken, Usuario nuevosDatos) {
        Usuario existente = usuarioRepository.findByEmail(emailToken)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no existente"));

        usuarioRepository.findByEmail(nuevosDatos.getEmail())
                .filter(usuario -> !usuario.getId().equals(existente.getId()))
                .ifPresent(usuario -> {
                    throw new ConflictException("El email ya está registrado");
                });

        usuarioRepository.findByUsuario(nuevosDatos.getUsuario())
                .filter(usuario -> !usuario.getId().equals(existente.getId()))
                .ifPresent(usuario -> {
                    throw new ConflictException("El nombre de usuario ya está registrado");
                });

        existente.setNombre(nuevosDatos.getNombre());
        existente.setUsuario(nuevosDatos.getUsuario());
        existente.setEmail(nuevosDatos.getEmail());
        return usuarioRepository.save(existente);
    }

    public void eliminarUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Usuario no existente"));
        usuarioRepository.delete(usuario);
    }
}
