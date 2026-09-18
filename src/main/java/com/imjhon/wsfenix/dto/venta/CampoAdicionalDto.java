package com.imjhon.wsfenix.dto.venta;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampoAdicionalDto {

    // Atributo @nombre del XML (fast-xml-parser lo entrega como "@nombre")
    @NotBlank(message = "El nombre del campo adicional es obligatorio")
    @JsonProperty("nombre")
    @JsonAlias({"@nombre", "nombreCampo"})
    private String nombre;

    // Texto del nodo <campoAdicional> ("#text" en fast-xml-parser, "texto" en otros)
    @NotBlank(message = "El valor del campo adicional es obligatorio")
    @JsonProperty("valor")
    @JsonAlias({"#text", "__text", "texto", "value"})
    private String valor;
}
