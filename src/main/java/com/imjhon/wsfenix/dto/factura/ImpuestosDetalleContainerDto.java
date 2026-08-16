package com.imjhon.wsfenix.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImpuestosDetalleContainerDto {

    @Valid
    @NotEmpty(message = "El desglose de impuestos del ítem es obligatorio")
    @JsonProperty("impuesto") // Mapea la etiqueta en singular del estándar del SRI
    private List<ImpuestoDetalleDto> impuesto = new ArrayList<>();
}
