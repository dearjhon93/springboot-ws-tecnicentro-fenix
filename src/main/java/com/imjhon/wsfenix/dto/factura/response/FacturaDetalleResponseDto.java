package com.imjhon.wsfenix.dto.factura.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDetalleResponseDto {

    private Long id;

    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String descripcion;

    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal precioTotalSinImpuesto;

    private List<DetalleImpuestoResponseDto> impuestos;
}