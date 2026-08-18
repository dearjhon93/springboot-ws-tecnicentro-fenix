package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.LocalDao;
import com.imjhon.wsfenix.dao.ProductoDao;
import com.imjhon.wsfenix.dao.ProductoLocalDao;
import com.imjhon.wsfenix.dto.ProductoDto;
import com.imjhon.wsfenix.dto.ProductoLocalDto;
import com.imjhon.wsfenix.dto.ProductoRequest;
import com.imjhon.wsfenix.dto.ProductoResponse;
import com.imjhon.wsfenix.entity.*;
import com.imjhon.wsfenix.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoDao repoProducto;

    @Autowired
    private ProductoLocalDao repoProductoLocal;

    @Autowired
    private LocalDao repoLocal;

    @GetMapping
    public ResponseEntity<?> getProductos() {

        List<ProductoDto> listaProd = new ArrayList<>();
        for (Producto p : repoProducto.findAllWithLocales()){
            ProductoDto res = new ProductoDto();
            res.setSecProducto(p.getId().getSecProducto());
            res.setCodProductoProveedor(p.getCodProductoProveedor());
            res.setDescripcion(p.getDescripcion());
            res.setPrecioVenta(p.getPrecioVenta());
            res.setCodEstado(p.getCodEstado());
            List<ProductoLocalDto> listapl = new ArrayList<>();
            for(ProductoLocal plDao : p.getLocales()){
                ProductoLocalDto pl = new ProductoLocalDto();
                pl.setSecLocal(plDao.getId().getSecLocal());
                pl.setCantidad(plDao.getCantidad());
                Local local = repoLocal.findByCodId(plDao.getId().getSecLocal());
                pl.setDesLocal(local.getNombreLocal());
                listapl.add(pl);
            }
            res.setLocales(listapl);
            listaProd.add(res);
        }
        ApiResponse<List<ProductoDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "PRODUCTOS ENCONTRADOS",
                listaProd);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(
            @RequestBody ProductoRequest prodReq
    ) {
        /*Valida si existe el COD producto PROVEEDOR, manda error si existe*/
        Producto existeProducto = repoProducto.findByCodProductoProveedor(
                prodReq.getCodProductoProveedor(),
                LocalDateTime.of(2999, 12, 31, 0, 0, 0)
        );
        if (existeProducto!=null){
            ApiResponse<?> response = new ApiResponse<>(
                    HttpStatus.CONFLICT.value(),
                    "ERROR: PRODUCTO EXISTENTE CODIGO "+existeProducto.getCodProductoProveedor(),
                    null);
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }

        /*Crear en PRODUCTO*/
        ProductoPk pk = new ProductoPk();
        Integer nuevaSecuencia = getSiguienteSecuencia();
        pk.setSecProducto(nuevaSecuencia);
        pk.setFechaFin(
                LocalDateTime.of(2999, 12, 31, 0, 0, 0)
        );
        Producto nuevoProducto = new Producto();
        nuevoProducto.setId(pk);
        nuevoProducto.setFechaInicio(LocalDateTime.now());
        nuevoProducto.setCodProductoProveedor(prodReq.getCodProductoProveedor());
        nuevoProducto.setDescripcion(prodReq.getDescripcion());
        nuevoProducto.setPrecioVenta(prodReq.getPrecioVenta());
        nuevoProducto.setCodEstado("ACT");
        nuevoProducto.setFechaIngreso(LocalDateTime.now());
        nuevoProducto.setCodUsuarioIngreso("1");
        nuevoProducto.setCodBarra(null);
        nuevoProducto = repoProducto.save(nuevoProducto);
        
        /*Crear en PRODUCTOLOCALES*/
        ProductoLocalPk plPk = new ProductoLocalPk();
        plPk.setSecLocal(prodReq.getSecLocal());
        plPk.setSecProducto(nuevaSecuencia);
        plPk.setFechaFin(
                LocalDateTime.of(2999, 12, 31, 0, 0, 0)
        );
        ProductoLocal nuevoProdLocal = new ProductoLocal();
        nuevoProdLocal.setId(plPk);
        nuevoProdLocal.setFechaInicio(LocalDateTime.now());
        nuevoProdLocal.setCantidad(prodReq.getCantidad());
        nuevoProdLocal.setFechaIngreso(LocalDateTime.now());
        nuevoProdLocal.setCodUsuarioIngreso("1");
        nuevoProdLocal = repoProductoLocal.save(nuevoProdLocal);

        ProductoResponse res = new ProductoResponse();
        res.setSecProducto(nuevaSecuencia);
        res.setCodProductoProveedor(nuevoProducto.getCodProductoProveedor());
        res.setDescripcion(nuevoProducto.getDescripcion());
        res.setSecLocal(nuevoProdLocal.getId().getSecLocal());
        res.setCantidad(nuevoProdLocal.getCantidad());
        ApiResponse<ProductoResponse> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "PRODUCTO CREADO CORRECTAMENTE",
                res);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public Integer getSiguienteSecuencia() {
        Integer maxActual = repoProducto.getMaxSecuencia();
        return maxActual + 1;
    }
}
