package com.imjhon.wsfenix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UltimaVentaDto {

    private String claveAcceso;

    // Numero legible "estab-ptoEmi-secuencial"
    private String numero;

    private LocalDate fechaEmision;

    private String cliente;

    private BigDecimal importeTotal;
}
