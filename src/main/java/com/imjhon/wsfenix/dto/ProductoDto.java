package com.imjhon.wsfenix.dto;

import com.imjhon.wsfenix.entity.ProductoLocal;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductoDto {
    private Integer secProducto;
    private String codProductoProveedor;
    private String descripcion;
    private BigDecimal precioVenta;
    private String codEstado;
    private List<ProductoLocalDto> locales;
}
