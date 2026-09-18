package com.imjhon.wsfenix.dto.venta.response;

import lombok.Data;

@Data
public class FacturaVentaAdicionalResponseDto {

    private Long idAdicional;
    private String nombreCampo;
    private String valor;
}
