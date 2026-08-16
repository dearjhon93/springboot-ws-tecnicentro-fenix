package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.ProveedorDao;
import com.imjhon.wsfenix.entity.Proveedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@CrossOrigin(origins = "https://vercel.app")
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorDao repoProveedor;

    @GetMapping
    public List<Proveedor> getProveedores(){
        return repoProveedor.findAll();
    }
}
