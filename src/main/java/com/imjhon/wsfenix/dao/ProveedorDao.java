package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.entity.Local;
import com.imjhon.wsfenix.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorDao extends JpaRepository<Proveedor,Long> {

    @Query("SELECT COALESCE(MAX(l.secProveedor), 0) FROM Proveedor l")
    Long getMaxSecuencia();
}
