package com.imjhon.wsfenix.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data // Genera getters, setters y constructors automáticamente si usas Lombok
public class InfoFacturaDto {

    @NotBlank(message = "La fecha de emisión es obligatoria")
    @JsonProperty("fechaEmision")
    private String fechaEmision; // Formato esperado en el controlador: "dd/MM/yyyy"

    @JsonProperty("dirEstablecimiento")
    private String dirEstablecimiento;

    @NotBlank(message = "El tipo de identificación del comprador es obligatorio")
    @JsonProperty("tipoIdentificacionComprador")
    private String tipoIdentificacionComprador; // 04: RUC, 05: Cédula, 06: Pasaporte

    @NotBlank(message = "La identificación del comprador es obligatoria")
    @JsonProperty("identificacionComprador")
    private String identificacionComprador;

    @NotBlank(message = "La razón social del comprador es obligatoria")
    @JsonProperty("razonSocialComprador")
    private String razonSocialComprador;

    @JsonProperty("direccionComprador")
    private String direccionComprador;

    @NotNull(message = "El total sin impuestos es obligatorio")
    @Min(value = 0, message = "El total sin impuestos no puede ser negativo")
    @JsonProperty("totalSinImpuestos")
    private Double totalSinImpuestos;

    @NotNull(message = "El total de descuento es obligatorio")
    @JsonProperty("totalDescuento")
    private Double totalDescuento = 0.0; // Inicializado en cero por defecto

    @NotNull(message = "El importe total es obligatorio")
    @Min(value = 0, message = "El importe total debe ser mayor a cero")
    @JsonProperty("importeTotal")
    private Double importeTotal;

    // FALTABA: Contenedor obligatorio de las formas de pago de la factura electrónico (SRI)
    @Valid
    @JsonProperty("pagos")
    private PagosContainerDto pagos;

}

