package com.imjhon.wsfenix.dto.venta.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FacturaVentaPagoResponseDto {

    private Long idPago;
    private String formaPago;
    private BigDecimal total;
    private BigDecimal plazo;
    private String unidadTiempo;
}
