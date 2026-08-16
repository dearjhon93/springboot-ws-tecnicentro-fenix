package com.imjhon.wsfenix.dto;

import lombok.Data;

@Data
public class ProductoResponse {
    private Integer secProducto;
    private String codProductoProveedor;
    private String descripcion;
    private Long secLocal;
    private Integer cantidad;
}
