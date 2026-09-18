package com.imjhon.wsfenix.dto.venta.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FacturaVentaDetalleImpuestoResponseDto {

    private Long idDetalleImpuesto;
    private String codigoImpuesto;
    private String codigoPorcentaje;
    private BigDecimal tarifa;
    private BigDecimal baseImponible;
    private BigDecimal valor;
}
