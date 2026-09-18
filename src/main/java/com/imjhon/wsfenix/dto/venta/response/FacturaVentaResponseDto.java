package com.imjhon.wsfenix.dto.venta.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FacturaVentaResponseDto {

    private String claveAcceso;
    private String numeroAutorizacion;
    private LocalDateTime fechaAutorizacion;
    private String establecimiento;
    private String puntoEmision;
    private String secuencial;
    private LocalDate fechaEmision;
    private BigDecimal totalSinImpuestos;
    private BigDecimal totalDescuento;
    private BigDecimal importeTotal;
    private ClienteResponseDto cliente;
    private List<FacturaVentaDetalleResponseDto> detalles = new ArrayList<>();
    private List<FacturaVentaPagoResponseDto> pagos = new ArrayList<>();
    private List<FacturaVentaAdicionalResponseDto> adicionales = new ArrayList<>();
}
