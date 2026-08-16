package com.imjhon.wsfenix.dto.factura;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ImpuestoDetalleDto {

    @NotNull(message = "El código de impuesto es obligatorio")
    private Integer codigo; // 2: IVA, 3: ICE, 5: IRBPNR

    @NotNull(message = "El código de porcentaje es obligatorio")
    private Integer codigoPorcentaje; // Para IVA -> 4: 15% (vigente), 0: 0%, 6: No Objeto, 7: Exento

    @NotNull(message = "La tarifa del impuesto es obligatoria")
    private Double tarifa; // Ej: 15.00 o 0.00

    @NotNull(message = "La base imponible es obligatoria")
    private Double baseImponible; // El subtotal del ítem antes de calcular este impuesto

    @NotNull(message = "El valor calculado del impuesto es obligatorio")
    private Double valor; // El monto monetario resultante del impuesto (Base Imponible * Tarifa)

}