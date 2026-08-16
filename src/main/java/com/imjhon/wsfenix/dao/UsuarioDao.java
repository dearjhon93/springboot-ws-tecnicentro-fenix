package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.entity.Local;
import com.imjhon.wsfenix.entity.Usuario;
import com.imjhon.wsfenix.entity.UsuarioPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, UsuarioPk> {

    @Override
    Optional<Usuario> findById(UsuarioPk id);

    Optional<Usuario> findByAlias(String alias);
}
