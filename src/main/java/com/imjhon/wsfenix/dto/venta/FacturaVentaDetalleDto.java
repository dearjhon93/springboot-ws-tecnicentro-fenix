package com.imjhon.wsfenix.dto.venta;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FacturaVentaDetalleDto {

    @NotBlank(message = "El codigo principal es obligatorio")
    @JsonProperty("codigoPrincipal")
    private String codigoPrincipal;

    @JsonProperty("codigoAuxiliar")
    private String codigoAuxiliar;

    @NotBlank(message = "La descripcion es obligatoria")
    @JsonProperty("descripcion")
    private String descripcion;

    @NotNull(message = "La cantidad es obligatoria")
    @JsonProperty("cantidad")
    private BigDecimal cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @JsonProperty("precioUnitario")
    private BigDecimal precioUnitario;

    @JsonProperty("descuento")
    private BigDecimal descuento = BigDecimal.ZERO;

    @NotNull(message = "El precio total sin impuesto es obligatorio")
    @JsonProperty("precioTotalSinImpuesto")
    private BigDecimal precioTotalSinImpuesto;

    // Nodo SRI: detalle -> impuestos -> impuesto (objeto unico o arreglo)
    @Valid
    @JsonProperty("impuestos")
    private FacturaVentaImpuestosContainerDto impuestos;
}
