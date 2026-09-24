package com.imjhon.wsfenix.dto;

import lombok.Data;

@Data
public class ProductoCantidadUpdateRequest {
    private Integer cantidad;
    private String observacion;
    private String codUsuarioModificacion;
}
