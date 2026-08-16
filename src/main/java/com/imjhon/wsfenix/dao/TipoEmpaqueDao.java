package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.entity.Local;
import com.imjhon.wsfenix.entity.TipoEmpaque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEmpaqueDao extends JpaRepository<TipoEmpaque,Long> {

    @Query("SELECT p FROM TipoEmpaque p WHERE p.codTipoEmpaque = :id")
    TipoEmpaque findByCodId(Long id);

    @Query("SELECT COALESCE(MAX(l.codTipoEmpaque), 0) FROM TipoEmpaque l")
    Long getMaxSecuencia();

}
