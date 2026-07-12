package com.example.apiusuario.Service;

import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.Respository.UsuarioRepository;
import com.example.apiusuario.dto.LoginResponse;
import com.example.apiusuario.dto.RegisterRequest;
import com.example.apiusuario.dto.UsuarioResponse;
import com.example.apiusuario.exception.ConflictException;
import com.example.apiusuario.exception.ForbiddenException;
import com.example.apiusuario.exception.ResourceNotFoundException;
import com.example.apiusuario.exception.UnauthorizedException;
import com.example.apiusuario.security.AuthenticatedUser;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ServicioUsuario {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse registrarUsuario(RegisterRequest registerRequest) {
        if (usuarioRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.nombre());
        usuario.setEmail(registerRequest.email());
        usuario.setPassword(passwordEncoder.encode(registerRequest.password()));
        usuario.setUsuario(registerRequest.usuario());
        usuarioRepository.save(usuario);
        String token = jwtService.generarToken(usuario);

        return new LoginResponse(
                "Usuario creado",
                token,
                new UsuarioResponse(
                        usuario.getId(),
                        usuario.getUsuario(),
                        usuario.getNombre(),
                        usuario.getEmail()
                )
        );
    }

    @Transactional
    public LoginResponse login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenException("Usuario no encontrado"));

        if (!passwordMatches(password, usuario)) {
            throw new UnauthorizedException("Usuario no encontrado");
        }

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(
                "Login exitoso",
                token,
                UsuarioResponse.from(usuario));
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Usuario obtenerUsuarioPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario).orElseThrow(() ->
                new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Usuario obtenerUsuarioAutenticado(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.id() == null) {
            throw new UnauthorizedException("No autenticado");
        }

        return usuarioRepository.findById(authenticatedUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Usuario actualizarUsuarioAutenticado(AuthenticatedUser authenticatedUser, Usuario nuevosDatos) {
        Usuario existente = obtenerUsuarioAutenticado(authenticatedUser);

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

    private boolean passwordMatches(String rawPassword, Usuario usuario) {
        String storedPassword = usuario.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (passwordEncoder.matches(rawPassword, storedPassword)) {
            return true;
        }

        if (storedPassword.equals(rawPassword)) {
            usuarioRepository.updatePasswordById(usuario.getId(), passwordEncoder.encode(rawPassword));
            return true;
        }

        return false;
    }
}
