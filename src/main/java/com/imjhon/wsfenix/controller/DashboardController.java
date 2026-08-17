package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.FacturaDao;
import com.imjhon.wsfenix.dao.LocalDao;
import com.imjhon.wsfenix.dao.ProductoDao;
import com.imjhon.wsfenix.dao.ProveedorDao;
import com.imjhon.wsfenix.dao.UsuarioDao;
import com.imjhon.wsfenix.dto.DashboardResumenDto;
import com.imjhon.wsfenix.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProductoDao repoProducto;

    @Autowired
    private ProveedorDao repoProveedor;

    @Autowired
    private LocalDao repoLocal;

    @Autowired
    private UsuarioDao repoUsuario;

    @Autowired
    private FacturaDao repoFactura;

    @GetMapping("/resumen")
    public ResponseEntity<?> getResumen() {
        DashboardResumenDto resumen = new DashboardResumenDto();
        resumen.setTotalProductos(repoProducto.countProductosActivos());
        resumen.setTotalProveedores(repoProveedor.count());
        resumen.setTotalLocales(repoLocal.count());
        resumen.setTotalUsuarios(repoUsuario.count());
        resumen.setTotalFacturas(repoFactura.count());
        resumen.setVentasTotales(repoFactura.sumImporteTotal());

        ApiResponse<DashboardResumenDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "RESUMEN DEL DASHBOARD OBTENIDO",
                resumen);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}