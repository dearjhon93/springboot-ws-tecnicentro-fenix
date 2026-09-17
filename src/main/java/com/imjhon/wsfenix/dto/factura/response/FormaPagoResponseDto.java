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
public class FormaPagoResponseDto {

    private Long id;

    private String formaPago;
    private BigDecimal total;
    private BigDecimal plazo;
    private String unidadTiempo;
}
