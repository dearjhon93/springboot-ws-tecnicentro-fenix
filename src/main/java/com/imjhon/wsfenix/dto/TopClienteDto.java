package com.imjhon.wsfenix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopClienteDto {

    private String idCliente;

    private String razonSocial;

    private BigDecimal totalComprado;

    private long cantidadFacturas;
}
