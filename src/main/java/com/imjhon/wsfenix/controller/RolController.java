package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.RolDao;
import com.imjhon.wsfenix.entity.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private RolDao repoRol;

    @GetMapping
    public List<Rol> getAll() {
        return repoRol.findAll();
    }

}
