package com.imjhon.wsfenix.dto.factura.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContribuyenteResponseDto {

    private Long id;
    private String tipoIdentificacion;
    private String identificacion;
    private String razonSocial;
    private String direccion;
}
