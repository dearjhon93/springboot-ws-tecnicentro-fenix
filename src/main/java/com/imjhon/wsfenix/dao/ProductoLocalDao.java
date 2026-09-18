package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.entity.Producto;
import com.imjhon.wsfenix.entity.ProductoLocal;
import com.imjhon.wsfenix.entity.ProductoLocalPk;
import com.imjhon.wsfenix.entity.ProductoPk;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductoLocalDao extends JpaRepository<ProductoLocal, ProductoLocalPk> {

    @Query("SELECT p FROM ProductoLocal p WHERE p.id.secLocal = :secLocal AND p.id.secProducto = :secProducto AND p.id.fechaFin = :fechaFin")
    ProductoLocal findById(Long secLocal, Long secProducto, LocalDateTime fechaFin);

    @Query("""
        SELECT pl
        FROM ProductoLocal pl
        WHERE pl.id.secProducto IN :secuencias
          AND pl.id.fechaFin >= CURRENT_TIMESTAMP
        ORDER BY pl.id.secProducto ASC,
                 pl.id.secLocal ASC
    """)
    List<ProductoLocal> findLocalesActivosByProductos(
            @Param("secuencias") List<Long> secuencias
    );


    @Query("""
    SELECT pl
    FROM ProductoLocal pl
    WHERE pl.id.secProducto IN :secuencias
    ORDER BY pl.id.secProducto ASC,
             pl.id.secLocal ASC,
             pl.id.fechaFin DESC
    """)
    List<ProductoLocal> findHistorialByProductos(
            @Param("secuencias") List<Long> secuencias
    );

    @Query("""
        SELECT COUNT(pl)
        FROM ProductoLocal pl
        WHERE pl.id.fechaFin >= CURRENT_TIMESTAMP
          AND COALESCE(pl.cantidad, 0) <= :umbral
    """)
    long countStockBajo(@Param("umbral") int umbral);

    @Query("""
        SELECT pl.id.secProducto, p.descripcion, pl.cantidad, pl.id.secLocal
        FROM ProductoLocal pl
        JOIN pl.producto p
        WHERE pl.id.fechaFin >= CURRENT_TIMESTAMP
          AND COALESCE(pl.cantidad, 0) <= :umbral
        ORDER BY COALESCE(pl.cantidad, 0) ASC
    """)
    List<Object[]> stockBajoDetalle(@Param("umbral") int umbral, Pageable pageable);

}


