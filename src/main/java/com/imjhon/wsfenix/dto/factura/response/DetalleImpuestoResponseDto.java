package com.imjhon.wsfenix.dto.factura.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleImpuestoResponseDto {

    private Long id;

    private Integer codigo;
    private Integer codigoPorcentaje;

    private BigDecimal tarifa;
    private BigDecimal baseInponible;
    private BigDecimal valor;
}
