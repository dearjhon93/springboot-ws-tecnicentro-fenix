package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.entity.Rol;
import com.imjhon.wsfenix.entity.RolPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RolDao extends JpaRepository<Rol, RolPk> {

    @Query("SELECT COALESCE(MAX(l.id.codRol), 0) FROM Rol l")
    Integer getMaxSecuencia();
}
