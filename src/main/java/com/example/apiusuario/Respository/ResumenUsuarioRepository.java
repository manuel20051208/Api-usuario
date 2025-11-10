package com.example.apiusuario.Respository;

import com.example.apiusuario.Model.UsuarioResumen;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

@Repository
public interface ResumenUsuarioRepository extends CrudRepository<UsuarioResumen, Long> {

    @Query(value = "SELECT * FROM vista_resumen_usuario", nativeQuery = true)
    List<UsuarioResumen> obtenerResumenUsuarios();
}