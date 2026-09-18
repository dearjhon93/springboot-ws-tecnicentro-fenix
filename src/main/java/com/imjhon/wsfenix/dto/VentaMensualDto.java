package com.imjhon.wsfenix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaMensualDto {

    // Periodo en formato "YYYY-MM" (ej: "2026-09")
    private String periodo;

    private BigDecimal total;
}
