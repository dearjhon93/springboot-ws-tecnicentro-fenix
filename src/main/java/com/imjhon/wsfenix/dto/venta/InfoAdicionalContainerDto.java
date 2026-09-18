package com.imjhon.wsfenix.dto.venta;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InfoAdicionalContainerDto {

    @Valid
    @JsonProperty("campoAdicional")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<CampoAdicionalDto> campoAdicional = new ArrayList<>();
}
