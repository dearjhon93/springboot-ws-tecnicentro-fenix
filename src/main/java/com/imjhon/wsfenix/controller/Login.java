package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.LocalDao;
import com.imjhon.wsfenix.dao.UsuarioDao;
import com.imjhon.wsfenix.dto.LoginRequest;
import com.imjhon.wsfenix.dto.LoginResponse;
import com.imjhon.wsfenix.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class Login {

    @Autowired
    private UsuarioDao repoUsuario;

    @PostMapping
    public LoginResponse login(
            @RequestBody LoginRequest request
    ) {
        Usuario user = repoUsuario.findByAlias(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.getClave().equals(request.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return new LoginResponse("Login exitoso", user.getAlias());
    }
}
