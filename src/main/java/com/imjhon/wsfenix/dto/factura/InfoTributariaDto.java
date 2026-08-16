package com.imjhon.wsfenix.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // Si usas Lombok, genera automáticamente getters, setters y constructors
public class InfoTributariaDto {

    @NotBlank(message = "El ambiente es obligatorio")
    @Size(min = 1, max = 1, message = "El ambiente debe tener 1 dígito")
    @JsonProperty("ambiente")
    private String ambiente; // 1: Pruebas, 2: Producción

    @NotBlank(message = "El tipo de emisión es obligatorio")
    @Size(min = 1, max = 1, message = "El tipo de emisión debe tener 1 dígito")
    @JsonProperty("tipoEmision")
    private String tipoEmision = "1"; // 1: Normal

    @NotBlank(message = "La razón social es obligatoria")
    @JsonProperty("razonSocial")
    private String razonSocial;

    @JsonProperty("nombreComercial")
    private String nombreComercial;

    @NotBlank(message = "El RUC es obligatorio")
    @Size(min = 13, max = 13, message = "El RUC debe tener exactamente 13 dígitos")
    @JsonProperty("ruc")
    private String ruc;

    @NotBlank(message = "La clave de acceso es obligatoria")
    @Size(min = 49, max = 49, message = "La clave de acceso debe tener exactamente 49 dígitos")
    @JsonProperty("claveAcceso")
    private String claveAcceso; // Clave de 49 caracteres generada para el SRI

    @NotBlank(message = "El código de documento es obligatorio")
    @Size(min = 2, max = 2, message = "El código de documento debe tener 2 dígitos")
    @JsonProperty("codDoc")
    private String codDoc = "01"; // 01: Factura

    @NotBlank(message = "El código de establecimiento es obligatorio")
    @Size(min = 3, max = 3, message = "El establecimiento debe tener exactamente 3 dígitos")
    @JsonProperty("estab")
    private String estab; // Ejemplo: "001"

    @NotBlank(message = "El punto de emisión es obligatorio")
    @Size(min = 3, max = 3, message = "El punto de emisión debe tener exactamente 3 dígitos")
    @JsonProperty("ptoEmi")
    private String ptoEmi; // Ejemplo: "002"

    @NotBlank(message = "El secuencial de la factura es obligatorio")
    @Size(min = 9, max = 9, message = "El secuencial debe tener exactamente 9 dígitos")
    @JsonProperty("secuencial")
    private String secuencial; // Ejemplo: "000000005"

    @NotBlank(message = "La dirección de la matriz es obligatoria")
    @JsonProperty("dirMatriz")
    private String dirMatriz;

}

