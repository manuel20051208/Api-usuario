package com.example.apiusuario.Respository;
import com.example.apiusuario.Model.Usuario;
import com.example.apiusuario.dto.LoginResponse;
import com.example.apiusuario.dto.RegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByUsuario(String usuario);

    @Modifying
    @Query("update Usuario u set u.password = :newPassword where u.id = :userId")
    void updatePasswordById(@Param("userId") Long userId, @Param("newPassword") String newPassword);
}
