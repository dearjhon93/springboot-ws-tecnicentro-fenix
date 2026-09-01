package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.entity.Producto;
import com.imjhon.wsfenix.entity.ProductoLocal;
import com.imjhon.wsfenix.entity.ProductoLocalPk;
import com.imjhon.wsfenix.entity.ProductoPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ProductoLocalDao extends JpaRepository<ProductoLocal, ProductoLocalPk> {

    @Query("SELECT p FROM ProductoLocal p WHERE p.id.secLocal = :secLocal AND p.id.secProducto = :secProducto AND p.id.fechaFin = :fechaFin")
    ProductoLocal findById(Long secLocal, Long secProducto, LocalDateTime fechaFin);
}
