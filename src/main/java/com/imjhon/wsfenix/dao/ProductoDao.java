package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.entity.Producto;
import com.imjhon.wsfenix.entity.ProductoPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductoDao extends JpaRepository<Producto, ProductoPk> {

    @Query("SELECT p FROM Producto p WHERE p.id.secProducto = :secuencia AND p.id.fechaFin = :fechaFin")
    Producto findByCodId(Long secuencia, LocalDateTime fechaFin);

    @Query("SELECT p FROM Producto p WHERE p.codProductoProveedor = :codigo AND p.id.fechaFin = :fechaFin")
    Producto findByCodProductoProveedor(String codigo, LocalDateTime fechaFin);

    @Query("SELECT COALESCE(MAX(l.id.secProducto), 0) FROM Producto l")
    Integer getMaxSecuencia();

    //@Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.locales")
    @Query("SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.locales l " +
            "WHERE (l.id.fechaFin >= CURRENT_DATE) and (p.id.fechaFin >= CURRENT_DATE) " +
            "ORDER BY l.id.secLocal ASC")
    List<Producto> findAllWithLocales();

    @Query("SELECT COUNT(DISTINCT p.id.secProducto) FROM Producto p WHERE p.id.fechaFin >= CURRENT_DATE")
    long countProductosActivos();
}
