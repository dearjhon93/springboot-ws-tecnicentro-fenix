package com.imjhon.wsfenix.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;


public class DetallesContainerDto {

    @Valid
    @NotEmpty(message = "La lista de detalles no puede estar vacía")
    // Esta anotación le dice a Spring/Jackson que busque exactamente la propiedad "detalle" en el JSON
    @JsonProperty("detalle")
    private List<FacturaDetalleDto> detalle = new ArrayList<>(); // Inicializada para evitar NullPointerException

    // Constructor vacío por defecto (Requerido por Jackson)
    public DetallesContainerDto() {
    }

    public List<FacturaDetalleDto> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<FacturaDetalleDto> detalle) {
        this.detalle = detalle;
    }
}
