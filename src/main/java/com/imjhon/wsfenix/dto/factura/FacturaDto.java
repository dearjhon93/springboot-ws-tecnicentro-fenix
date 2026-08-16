package com.imjhon.wsfenix.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data // Genera automáticamente getters, setters, toString, equals y hashCode si usas Lombok
public class FacturaDto {

    @NotBlank(message = "El código del local es obligatorio")
    @JsonProperty("codigoLocal")
    private String codigoLocal;

    @NotNull(message = "La información tributaria es obligatoria")
    @Valid
    @JsonProperty("infoTributaria") // Asegura el mapeo exacto desde el JSON del frontend
    private InfoTributariaDto infoTributaria;

    @NotNull(message = "La información de la factura es obligatoria")
    @Valid
    @JsonProperty("infoFactura") // Mapea el nodo de subtotales, cliente y pagos
    private InfoFacturaDto infoFactura;

    @NotNull(message = "Los detalles de la factura son obligatorios")
    @Valid
    @JsonProperty("detalles") // Mapea el contenedor de los ítems de la factura
    private DetallesContainerDto detalles;

}

