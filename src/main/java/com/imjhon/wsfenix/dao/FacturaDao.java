package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.dto.factura.dao.Factura;
import com.imjhon.wsfenix.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FacturaDao extends JpaRepository<Factura,Long> {

    // Permite saber si la factura ya fue registrada por su clave de acceso (única)
    boolean existsByClaveAcceso(String claveAcceso);

    // Total acumulado de las facturas registradas
    @Query("SELECT COALESCE(SUM(f.importeTotal), 0) FROM Factura f")
    BigDecimal sumImporteTotal();

    // Total de compras (facturas de proveedor) en un rango de fechas
    @Query("""
        SELECT COALESCE(SUM(f.importeTotal), 0)
        FROM Factura f
        WHERE f.fechaEmision BETWEEN :desde AND :hasta
    """)
    BigDecimal sumImporteTotalEntre(LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(MAX(f.id), 0) FROM Factura f")
    Integer getMaxSecuencia();

    @Query("SELECT COUNT(f) FROM Factura f WHERE f.fechaEmision BETWEEN :desde AND :hasta")
    long countEntre(LocalDate desde, LocalDate hasta);

    @Query("""
        SELECT YEAR(f.fechaEmision), MONTH(f.fechaEmision), COALESCE(SUM(f.importeTotal), 0)
        FROM Factura f
        WHERE f.fechaEmision >= :desde
        GROUP BY YEAR(f.fechaEmision), MONTH(f.fechaEmision)
        ORDER BY YEAR(f.fechaEmision), MONTH(f.fechaEmision)
    """)
    List<Object[]> comprasPorMes(LocalDate desde);

    @Query("""
        SELECT DISTINCT f
        FROM Factura f
        LEFT JOIN FETCH f.contribuyente
        LEFT JOIN FETCH f.detalles d
        LEFT JOIN FETCH d.impuestos
        LEFT JOIN FETCH f.formasPago
        ORDER BY f.id DESC
    """)
    List<Factura> findTodasConDetalles();

    // Listado seguro: trae cabecera + contribuyente; detalles, impuestos y
    // formas de pago se cargan por lote (@BatchSize) al mapear el DTO.
    @Query("""
        SELECT DISTINCT f
        FROM Factura f
        LEFT JOIN FETCH f.contribuyente
        ORDER BY f.id DESC
    """)
    List<Factura> findTodasConContribuyente();
}
