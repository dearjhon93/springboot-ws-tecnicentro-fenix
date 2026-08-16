package com.imjhon.wsfenix.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {
    private String codProductoProveedor;
    private String descripcion;
    private Integer codTipoempaque;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Long secLocal;
    private Integer cantidad;
}
