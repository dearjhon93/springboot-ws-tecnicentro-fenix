package com.imjhon.wsfenix.dto.venta.response;

import lombok.Data;

@Data
public class ClienteResponseDto {

    private String idCliente;
    private String tipoIdentificacion;
    private String razonSocial;
    private String direccion;
}
