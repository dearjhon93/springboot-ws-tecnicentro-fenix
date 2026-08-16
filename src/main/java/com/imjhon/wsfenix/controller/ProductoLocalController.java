package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.ProductoDao;
import com.imjhon.wsfenix.dao.ProductoLocalDao;
import com.imjhon.wsfenix.entity.Producto;
import com.imjhon.wsfenix.entity.ProductoLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@CrossOrigin(origins = "https://vercel.app")
@RequestMapping("/productoslocales")
public class ProductoLocalController {

    @Autowired
    private ProductoLocalDao repoProdLocal;

    @GetMapping
    public List<ProductoLocal> getProductosLocales(){
        return repoProdLocal.findAll();
    }
}
