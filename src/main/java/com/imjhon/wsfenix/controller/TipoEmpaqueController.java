package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.TipoEmpaqueDao;
import com.imjhon.wsfenix.entity.TipoEmpaque;
import com.imjhon.wsfenix.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tiposempaques")
public class TipoEmpaqueController {

    @Autowired
    private TipoEmpaqueDao repoTipoEmpaque;

    @GetMapping
    public List<TipoEmpaque> getTiposEmpaques(){
        return repoTipoEmpaque.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crearTipoEmpaque(
            @RequestBody TipoEmpaque tipo
    ) {
        tipo.setCodTipoEmpaque(getSiguienteSecuencia());
        tipo.setCodEstado("ACT");

        TipoEmpaque nuevoTipoEmpaque = repoTipoEmpaque.save(tipo);
        ApiResponse<TipoEmpaque> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "TIPO CREADO CORRECTAMENTE",
                nuevoTipoEmpaque);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> actualizarTipoEmpaque(
            @RequestBody TipoEmpaque tipo
    ) {
        TipoEmpaque entidadExistente = repoTipoEmpaque.findByCodId(tipo.getCodTipoEmpaque());
        if (entidadExistente!=null) {
            entidadExistente.setDescripcion(tipo.getDescripcion());
            repoTipoEmpaque.save(entidadExistente);
        }

        ApiResponse<TipoEmpaque> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "TIPO ACTUALIZADO CORRECTAMENTE",
                entidadExistente);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{codTipoEmpaque}")
    public ResponseEntity<?> borrarTipoEmpaque(
            @PathVariable Long codTipoEmpaque
    ) {
        TipoEmpaque entidadExistente = repoTipoEmpaque.findByCodId(codTipoEmpaque);
        if (entidadExistente!=null) {
            repoTipoEmpaque.delete(entidadExistente);
        }

        /*Validar si el tipo empaque existe en PPoductos, NO BORRAR*/

        ApiResponse<TipoEmpaque> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "TIPO ELIMINADO CORRECTAMENTE",
                entidadExistente);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public Long getSiguienteSecuencia() {
        Long maxActual = repoTipoEmpaque.getMaxSecuencia();
        return maxActual + 1;
    }
}
