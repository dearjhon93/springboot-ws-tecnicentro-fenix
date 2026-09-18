package com.imjhon.wsfenix.dto.venta;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FacturaVentaDetalleImpuestoDto {

    @NotBlank(message = "El codigo de impuesto es obligatorio")
    @JsonProperty("codigo")
    private String codigoImpuesto; // 2=IVA, 3=ICE, 5=IRBPNR

    @NotBlank(message = "El codigo de porcentaje es obligatorio")
    @JsonProperty("codigoPorcentaje")
    private String codigoPorcentaje; // 0=0%, 2=12%, 4=15%, etc.

    @NotNull(message = "La tarifa es obligatoria")
    @JsonProperty("tarifa")
    private BigDecimal tarifa;

    @NotNull(message = "La base imponible es obligatoria")
    @JsonProperty("baseImponible")
    private BigDecimal baseImponible;

    @NotNull(message = "El valor del impuesto es obligatorio")
    @JsonProperty("valor")
    private BigDecimal valor;
}
