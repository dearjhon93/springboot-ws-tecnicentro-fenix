package com.imjhon.wsfenix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenDto {

    private long totalProductos;
    private long totalProveedores;
    private long totalLocales;
    private long totalUsuarios;
    private long totalFacturas;
    private BigDecimal ventasTotales;

}