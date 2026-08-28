package com.imjhon.wsfenix.dto.factura;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FacturaDetalleDto {

    @NotBlank(message = "El código principal es obligatorio")
    private String codigoPrincipal;

    // El SRI lo permite opcional (para código de barras o del proveedor)
    private String codigoAuxiliar;

    @NotBlank(message = "La descripción del producto es obligatoria")
    private String descripcion;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser mayor a cero")
    private Double cantidad;

    @NotNull(message = "La cantidad modificada es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser mayor a cero")
    private Double cantidadModificada;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 0, message = "El precio unitario no puede ser negativo")
    private Double precioUnitario;

    // Descuento aplicado al ítem por el emisor (si no aplica, el frontend debe enviar 0.0)
    private Double descuento = 0.0;

    @NotNull(message = "El precio total sin impuestos es obligatorio")
    private Double precioTotalSinImpuesto;

    // FALTABA: Objeto contenedor de los impuestos del ítem (IVA, ICE) obligatorios para el backend
    @Valid
    private ImpuestosDetalleContainerDto impuestos;

    // Campo enriquecido en Angular desde el FormArray
    @NotNull(message = "El precio de venta (PVP) es obligatorio por cada producto")
    @Min(value = 0, message = "El PVP debe ser mayor a cero")
    private Double precioVentaPvp;

    @NotNull(message = "El porcentaje del local al producto es obligatorio")
    private Integer porcentajeLocal;

}
