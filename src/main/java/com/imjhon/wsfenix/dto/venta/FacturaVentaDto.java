package com.imjhon.wsfenix.dto.venta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.imjhon.wsfenix.dto.factura.InfoFacturaDto;
import com.imjhon.wsfenix.dto.factura.InfoTributariaDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FacturaVentaDto {

    // Local que vende (origen del stock a descontar)
    @NotBlank(message = "El codigo del local es obligatorio")
    @JsonProperty("codigoLocal")
    private String codigoLocal;

    @NotNull(message = "La informacion tributaria es obligatoria")
    @Valid
    @JsonProperty("infoTributaria")
    private InfoTributariaDto infoTributaria;

    @NotNull(message = "La informacion de la factura es obligatoria")
    @Valid
    @JsonProperty("infoFactura")
    private InfoFacturaDto infoFactura;

    @NotNull(message = "Los detalles de la factura son obligatorios")
    @Valid
    @JsonProperty("detalles")
    private FacturaVentaDetallesContainerDto detalles;

    // Bloque opcional SRI: infoAdicional -> campoAdicional
    @Valid
    @JsonProperty("infoAdicional")
    private InfoAdicionalContainerDto infoAdicional;

    // XML completo en texto plano para auditoria (opcional)
    @JsonProperty("xmlOriginal")
    private String xmlOriginal;
}
