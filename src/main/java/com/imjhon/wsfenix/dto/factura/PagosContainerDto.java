package com.imjhon.wsfenix.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class PagosContainerDto {
    @Valid
    @NotEmpty(message = "Debe registrar al menos una forma de pago")
    @JsonProperty("pago")
    private List<PagoDto> pago = new ArrayList<>();
}

