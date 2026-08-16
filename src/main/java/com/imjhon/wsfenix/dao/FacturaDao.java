package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.dto.factura.dao.Factura;
import com.imjhon.wsfenix.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaDao extends JpaRepository<Factura,Long> {

    // Permite saber si la factura ya fue registrada por su clave de acceso (única)
    boolean existsByClaveAcceso(String claveAcceso);
}
