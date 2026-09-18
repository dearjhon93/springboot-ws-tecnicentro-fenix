package com.imjhon.wsfenix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoStockBajoDto {

    private Long secProducto;

    private String descripcion;

    private Integer cantidad;

    private Long secLocal;
}
