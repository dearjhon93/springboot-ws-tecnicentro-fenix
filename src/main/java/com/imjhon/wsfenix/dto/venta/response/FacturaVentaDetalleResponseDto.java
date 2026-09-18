package com.imjhon.wsfenix.dto.venta.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class FacturaVentaDetalleResponseDto {

    private Long idDetalle;
    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal precioTotalSinImpuesto;
    private List<FacturaVentaDetalleImpuestoResponseDto> impuestos = new ArrayList<>();
}
