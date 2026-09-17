package com.imjhon.wsfenix.dto.factura.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponseDto {

    private Long id;

    private String ambiente;
    private String tipoEmision;
    private String razonSocial;
    private String nombreComercial;
    private String ruc;
    private String claveAcceso;
    private String codDoc;
    private String estab;
    private String ptoEmi;
    private String secuencial;
    private String dirMatriz;

    private String dirEstablecimiento;
    private LocalDate fechaEmision;
    private BigDecimal totalSinImpuestos;
    private BigDecimal totalDescuento;
    private BigDecimal importeTotal;

    private String estadoSri;
    private LocalDateTime fechaAutorizacion;
    private String numeroAutorizacion;

    private ContribuyenteResponseDto contribuyente;

    private List<FacturaDetalleResponseDto> detalles;

    private List<FormaPagoResponseDto> formasPago;
}
