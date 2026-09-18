package com.imjhon.wsfenix.dto.venta;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FacturaVentaDetallesContainerDto {

    @Valid
    @NotEmpty(message = "La lista de detalles no puede estar vacia")
    @JsonProperty("detalle")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<FacturaVentaDetalleDto> detalle = new ArrayList<>();
}
