package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.FacturaDao;
import com.imjhon.wsfenix.dao.LocalDao;
import com.imjhon.wsfenix.dao.ProductoDao;
import com.imjhon.wsfenix.dao.ProductoLocalDao;
import com.imjhon.wsfenix.dto.*;
import com.imjhon.wsfenix.dto.factura.dao.Factura;
import com.imjhon.wsfenix.dto.response.PageResponse;
import com.imjhon.wsfenix.entity.*;
import com.imjhon.wsfenix.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoDao repoProducto;

    @Autowired
    private ProductoLocalDao repoProductoLocal;

    @Autowired
    private LocalDao repoLocal;

    @Autowired
    private FacturaDao repoFactura;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductoDto>>> getProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {

        // Validar paginación
        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        // Limpiar búsqueda
        if (search != null) {
            search = search.trim();

            if (search.isEmpty()) {
                search = null;
            }
        }

        Pageable pageable = PageRequest.of(page, size);

        // Obtener productos activos
        Page<Producto> productosPage =
                repoProducto.findProductosActivos(
                        search,
                        pageable
                );

        List<Producto> productos = productosPage.getContent();

        // Si no existen productos
        if (productos.isEmpty()) {

            PageResponse<ProductoDto> pageResponse =
                    new PageResponse<>(
                            Collections.emptyList(),
                            productosPage.getNumber(),
                            productosPage.getSize(),
                            productosPage.getTotalElements(),
                            productosPage.getTotalPages(),
                            productosPage.isFirst(),
                            productosPage.isLast()
                    );

            ApiResponse<PageResponse<ProductoDto>> response =
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "NO SE ENCONTRARON PRODUCTOS",
                            pageResponse
                    );

            return ResponseEntity.ok(response);
        }

        // =========================================================
        // 1. Obtener secuencias de productos
        // =========================================================

        List<Long> secuenciasProductos = productos.stream()
                .map(producto -> producto.getId().getSecProducto())
                .toList();


        // =========================================================
        // 2. Obtener ProductoLocal actuales
        // =========================================================

        List<ProductoLocal> productosLocales =
                repoProductoLocal.findLocalesActivosByProductos(
                        secuenciasProductos
                );


        // =========================================================
        // 3. Obtener historial de ProductoLocal
        // =========================================================

        List<ProductoLocal> historialProductoLocal =
                repoProductoLocal.findHistorialByProductos(
                        secuenciasProductos
                );


        // =========================================================
        // 4. Agrupar locales actuales por producto
        // =========================================================

        Map<Long, List<ProductoLocal>> localesPorProducto =
                productosLocales.stream()
                        .collect(Collectors.groupingBy(
                                pl -> pl.getId().getSecProducto()
                        ));


        // =========================================================
        // 5. Agrupar historial por producto
        // =========================================================

        Map<Long, List<ProductoLocal>> historialPorProducto =
                historialProductoLocal.stream()
                        .collect(Collectors.groupingBy(
                                pl -> pl.getId().getSecProducto()
                        ));


        // =========================================================
        // 6. Obtener IDs de todos los locales
        // =========================================================

        Set<Long> secuenciasLocales = new HashSet<>();

        productosLocales.forEach(pl ->
                secuenciasLocales.add(
                        pl.getId().getSecLocal()
                )
        );

        historialProductoLocal.forEach(pl ->
                secuenciasLocales.add(
                        pl.getId().getSecLocal()
                )
        );


        // =========================================================
        // 7. Obtener información de los locales
        // =========================================================

        List<Local> locales = secuenciasLocales.isEmpty()
                ? Collections.emptyList()
                : repoLocal.findAllById(secuenciasLocales);


        Map<Long, Local> localesMap = locales.stream()
                .collect(Collectors.toMap(
                        Local::getSecLocal,
                        local -> local
                ));


        // =========================================================
        // 7.1 Obtener números completos de factura
        // (estab-ptoEmi-secuencial) para idFactura de locales,
        // historial y producto. Se mantiene idFactura y se agrega
        // numFactura sin romper compatibilidad.
        // =========================================================

        Set<Long> idsFacturas = new HashSet<>();

        productos.forEach(p -> {
            if (p.getCodIdFactura() != null) {
                idsFacturas.add(p.getCodIdFactura());
            }
        });

        productosLocales.forEach(pl -> {
            if (pl.getIdFactura() != null) {
                idsFacturas.add(pl.getIdFactura());
            }
        });

        historialProductoLocal.forEach(pl -> {
            if (pl.getIdFactura() != null) {
                idsFacturas.add(pl.getIdFactura());
            }
        });

        Map<Long, String> numFacturaMap = idsFacturas.isEmpty()
                ? Collections.emptyMap()
                : repoFactura.findAllById(idsFacturas).stream()
                .collect(Collectors.toMap(
                        Factura::getId,
                        this::formatearNumeroFactura,
                        (a, b) -> a
                ));


        // =========================================================
        // 8. Convertir productos a DTO
        // =========================================================

        List<ProductoDto> listaProd = productos.stream()
                .map(producto -> {

                    Long secProducto =
                            producto.getId().getSecProducto();

                    // Locales actuales de este producto
                    List<ProductoLocal> localesProducto =
                            localesPorProducto.getOrDefault(
                                    secProducto,
                                    Collections.emptyList()
                            );

                    // Historial únicamente de este producto
                    List<ProductoLocal> historialProducto =
                            historialPorProducto.getOrDefault(
                                    secProducto,
                                    Collections.emptyList()
                            );

                    return convertirProductoDto(
                            producto,
                            localesProducto,
                            historialProducto,
                            localesMap,
                            numFacturaMap
                    );
                })
                .toList();


        // =========================================================
        // 9. Construir respuesta paginada
        // =========================================================

        PageResponse<ProductoDto> pageResponse =
                new PageResponse<>(
                        listaProd,
                        productosPage.getNumber(),
                        productosPage.getSize(),
                        productosPage.getTotalElements(),
                        productosPage.getTotalPages(),
                        productosPage.isFirst(),
                        productosPage.isLast()
                );


        ApiResponse<PageResponse<ProductoDto>> response =
                new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "PRODUCTOS ENCONTRADOS",
                        pageResponse
                );

        return ResponseEntity.ok(response);
    }

    /*@GetMapping
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
            res.setNomProveedor("");
            listaProd.add(res);
        }
        ApiResponse<List<ProductoDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "PRODUCTOS ENCONTRADOS",
                listaProd);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }*/

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


    private ProductoDto convertirProductoDto(
            Producto producto,
            List<ProductoLocal> localesProducto,
            List<ProductoLocal> historialProducto,
            Map<Long, Local> localesMap,
            Map<Long, String> numFacturaMap
    ) {

        ProductoDto dto = new ProductoDto();

        dto.setSecProducto(
                producto.getId().getSecProducto()
        );

        dto.setCodProductoProveedor(
                producto.getCodProductoProveedor()
        );

        dto.setDescripcion(
                producto.getDescripcion()
        );

        dto.setPrecioVenta(
                producto.getPrecioVenta()
        );

        dto.setCodEstado(
                producto.getCodEstado()
        );

        dto.setNomProveedor("");

        dto.setIdFactura(
                producto.getCodIdFactura()
        );

        dto.setNumFactura(
                producto.getCodIdFactura() != null
                        ? numFacturaMap.get(producto.getCodIdFactura())
                        : null
        );


        // =========================================================
        // Locales actuales
        // =========================================================

        List<ProductoLocalDto> localesDto =
                localesProducto.stream()
                        .map(pl -> {

                            ProductoLocalDto localDto =
                                    new ProductoLocalDto();

                            Long secLocal =
                                    pl.getId().getSecLocal();

                            localDto.setSecLocal(secLocal);
                            localDto.setCantidad(pl.getCantidad());
                            localDto.setIdFactura(pl.getIdFactura());
                            localDto.setNumFactura(
                                    pl.getIdFactura() != null
                                            ? numFacturaMap.get(pl.getIdFactura())
                                            : null
                            );

                            Local local =
                                    localesMap.get(secLocal);

                            if (local != null) {
                                localDto.setDesLocal(
                                        local.getNombreLocal()
                                );
                            }

                            return localDto;
                        })
                        .toList();

        dto.setLocales(localesDto);


        // =========================================================
        // Historial
        // =========================================================

        List<ProductoHistorialDto> historialDto =
                historialProducto.stream()
                        .map(pl -> {

                            ProductoHistorialDto historial =
                                    new ProductoHistorialDto();

                            Long secLocal =
                                    pl.getId().getSecLocal();

                            historial.setSecLocal(secLocal);
                            historial.setCantidad(pl.getCantidad());

                            historial.setFechaInicio(
                                    pl.getFechaInicio()
                            );

                            historial.setFechaFin(
                                    pl.getId().getFechaFin()
                            );

                            historial.setFechaModificacion(
                                    pl.getFechaModificacion()
                            );

                            // AHORA SALE DIRECTAMENTE DE ProductoLocal
                            historial.setIdFactura(
                                    pl.getIdFactura()
                            );

                            historial.setNumFactura(
                                    pl.getIdFactura() != null
                                            ? numFacturaMap.get(pl.getIdFactura())
                                            : null
                            );

                            Local local =
                                    localesMap.get(secLocal);

                            if (local != null) {
                                historial.setDesLocal(
                                        local.getNombreLocal()
                                );
                            }

                            return historial;
                        })
                        .toList();

        dto.setHistorial(historialDto);

        return dto;
    }

    /**
     * Número completo de factura SRI: estab-ptoEmi-secuencial
     * (ej: 001-001-000000123). Si falta alguna parte se devuelve
     * lo disponible sin romper.
     */
    private String formatearNumeroFactura(Factura factura) {
        if (factura == null) {
            return null;
        }

        String estab = factura.getEstab() != null ? factura.getEstab() : "";
        String ptoEmi = factura.getPtoEmi() != null ? factura.getPtoEmi() : "";
        String secuencial = factura.getSecuencial() != null ? factura.getSecuencial() : "";

        if (estab.isEmpty() && ptoEmi.isEmpty() && secuencial.isEmpty()) {
            return null;
        }

        return estab + "-" + ptoEmi + "-" + secuencial;
    }
}
