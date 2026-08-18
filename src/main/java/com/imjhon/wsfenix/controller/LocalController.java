package com.imjhon.wsfenix.controller;


import com.imjhon.wsfenix.dao.LocalDao;
import com.imjhon.wsfenix.util.ApiResponse;
import com.imjhon.wsfenix.entity.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/locales")
public class LocalController {

    @Autowired
    private LocalDao repoLocal;

    @GetMapping
    public List<Local> getLocales(){
        return repoLocal.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crearLocal(
            @RequestBody Local local
    ) {
        local.setSecLocal(getSiguienteSecuencia());
        local.setCod_estado("ACT");
        if ((local.getNombreLocal() == null) || local.getNombreLocal().isEmpty()){
            ApiResponse<Local> response = new ApiResponse<>(HttpStatus.NOT_ACCEPTABLE.value(),
                    "ERROR: NOMBRE DE LOCAL VACIO");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        Local nuevolocal = repoLocal.save(local);
        ApiResponse<Local> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "LOCAL CREADO CORRECTAMENTE",
                nuevolocal);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public Long getSiguienteSecuencia() {
        Long maxActual = repoLocal.getMaxSecuencia();
        return maxActual + 1;
    }

}
