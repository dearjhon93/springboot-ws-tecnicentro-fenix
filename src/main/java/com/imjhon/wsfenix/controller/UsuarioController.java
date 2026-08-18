package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.UsuarioDao;
import com.imjhon.wsfenix.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {


    @Autowired
    private UsuarioDao repoUsuario;

    @GetMapping
    public List<Usuario> getAll() {
        return repoUsuario.findAll();
    }

}
