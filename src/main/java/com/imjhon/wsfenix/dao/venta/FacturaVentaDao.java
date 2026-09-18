package com.imjhon.wsfenix.dao.venta;

import com.imjhon.wsfenix.entity.venta.FacturaVenta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FacturaVentaDao extends JpaRepository<FacturaVenta, String> {

    // La clave de acceso SRI es unica: evita duplicados desde Angular
    boolean existsByClaveAcceso(String claveAcceso);

    @Query("SELECT COALESCE(SUM(f.importeTotal), 0) FROM FacturaVenta f")
    BigDecimal sumImporteTotal();

    @Query("""
        SELECT COALESCE(SUM(f.importeTotal), 0)
        FROM FacturaVenta f
        WHERE f.fechaEmision BETWEEN :desde AND :hasta
    """)
    BigDecimal sumImporteTotalEntre(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("""
        SELECT YEAR(f.fechaEmision), MONTH(f.fechaEmision), COALESCE(SUM(f.importeTotal), 0)
        FROM FacturaVenta f
        WHERE f.fechaEmision >= :desde
        GROUP BY YEAR(f.fechaEmision), MONTH(f.fechaEmision)
        ORDER BY YEAR(f.fechaEmision), MONTH(f.fechaEmision)
    """)
    List<Object[]> ventasPorMes(@Param("desde") LocalDate desde);

    @Query("SELECT COUNT(f) FROM FacturaVenta f WHERE f.fechaEmision BETWEEN :desde AND :hasta")
    long countEntre(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("""
        SELECT f.fechaEmision, COALESCE(SUM(f.importeTotal), 0)
        FROM FacturaVenta f
        WHERE f.fechaEmision >= :desde
        GROUP BY f.fechaEmision
        ORDER BY f.fechaEmision
    """)
    List<Object[]> ventasPorDia(@Param("desde") LocalDate desde);

    List<FacturaVenta> findTop5ByOrderByFechaEmisionDesc();

    @Query("""
        SELECT f.cliente.idCliente, f.cliente.razonSocial,
               COALESCE(SUM(f.importeTotal), 0), COUNT(f)
        FROM FacturaVenta f
        GROUP BY f.cliente.idCliente, f.cliente.razonSocial
        ORDER BY SUM(f.importeTotal) DESC
    """)
    List<Object[]> topClientes(Pageable pageable);

    // Listado: trae cabecera + cliente; detalles, impuestos, pagos y
    // adicionales se cargan por lote (@BatchSize) al mapear el DTO.
    @Query("""
        SELECT DISTINCT f
        FROM FacturaVenta f
        LEFT JOIN FETCH f.cliente
        ORDER BY f.fechaEmision DESC, f.claveAcceso DESC
    """)
    List<FacturaVenta> findTodasConCliente();
}
