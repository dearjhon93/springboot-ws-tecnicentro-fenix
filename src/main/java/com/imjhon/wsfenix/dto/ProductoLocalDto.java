package com.imjhon.wsfenix.dto;

import lombok.Data;

@Data
public class ProductoLocalDto {
    private long secLocal;
    private String desLocal;
    private Integer cantidad;
    private String observacion;
    private Long idFactura;
    private String numFactura;
}
