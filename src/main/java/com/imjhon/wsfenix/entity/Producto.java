package com.imjhon.wsfenix.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "productos")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Producto {

    @EmbeddedId
    private ProductoPk id;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "cod_barra")
    private String codBarra;

    @Column(name = "cod_producto_proveedor")
    private String codProductoProveedor;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio_venta")
    private BigDecimal precioVenta;

    @Column(name = "cod_estado")
    private String codEstado;

    //@JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_ingreso")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaIngreso;

    @Column(name = "cod_usuario_ingreso")
    private String codUsuarioIngreso;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "cod_usuario_modificacion")
    private String codUsuarioModificacion;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ProductoLocal> locales;
}
