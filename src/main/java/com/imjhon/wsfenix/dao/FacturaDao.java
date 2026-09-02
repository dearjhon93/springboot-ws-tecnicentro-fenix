package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.dto.factura.dao.Factura;
import com.imjhon.wsfenix.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface FacturaDao extends JpaRepository<Factura,Long> {

    // Permite saber si la factura ya fue registrada por su clave de acceso (única)
    boolean existsByClaveAcceso(String claveAcceso);

    // Total acumulado de las facturas registradas
    @Query("SELECT COALESCE(SUM(f.importeTotal), 0) FROM Factura f")
    BigDecimal sumImporteTotal();

    @Query("SELECT COALESCE(MAX(f.id), 0) FROM Factura f")
    Integer getMaxSecuencia();
}
