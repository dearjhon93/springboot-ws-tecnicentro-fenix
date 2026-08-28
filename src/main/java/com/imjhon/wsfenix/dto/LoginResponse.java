package com.imjhon.wsfenix.dto;

import com.imjhon.wsfenix.entity.Usuario;
import lombok.Data;

@Data
public class LoginResponse {
    private String mensaje;
    private Usuario usuario;

    public LoginResponse(String mensaje, Usuario usuario) {
        this.mensaje = mensaje;
        this.usuario = usuario;
    }
}
