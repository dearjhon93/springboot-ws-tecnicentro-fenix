package com.imjhon.wsfenix.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoDto {
    @NotBlank(message = "La forma de pago es obligatoria")
    @JsonProperty("formaPago")
    private String formaPago; // Código SRI, ej: "01" (Sin utilización del sistema financiero), "20" (Otros)

    @NotNull(message = "El total de la forma de pago es obligatorio")
    @JsonProperty("total")
    private Double total;

    @JsonProperty("plazo")
    private Double plazo;

    @JsonProperty("unidadTiempo")
    private String unidadTiempo;
}

