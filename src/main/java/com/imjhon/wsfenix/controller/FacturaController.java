package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.ContribuyenteDao;
import com.imjhon.wsfenix.dao.FacturaDao;
import com.imjhon.wsfenix.dao.ProductoDao;
import com.imjhon.wsfenix.dao.ProductoLocalDao;
import com.imjhon.wsfenix.dto.ProductoRequest;
import com.imjhon.wsfenix.dto.ProductoResponse;
import com.imjhon.wsfenix.dto.factura.FacturaDetalleDto;
import com.imjhon.wsfenix.dto.factura.FacturaDto;
import com.imjhon.wsfenix.dto.factura.ImpuestoDetalleDto;
import com.imjhon.wsfenix.dto.factura.PagoDto;
import com.imjhon.wsfenix.dto.factura.dao.*;
import com.imjhon.wsfenix.dto.factura.response.*;
import com.imjhon.wsfenix.entity.Producto;
import com.imjhon.wsfenix.entity.ProductoLocal;
import com.imjhon.wsfenix.entity.ProductoLocalPk;
import com.imjhon.wsfenix.entity.ProductoPk;
import com.imjhon.wsfenix.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private ProductoDao repoProducto;

    @Autowired
    private ProductoLocalDao repoProductoLocal;

    @Autowired
    private FacturaDao repoFactura;

    @Autowired
    private ContribuyenteDao repoContribuyente;


    @Transactional(readOnly = true)
    @GetMapping("/compras")
    public ResponseEntity<?> obtenerFacturasCompras() {

        List<Factura> facturas =
                repoFactura.findTodasConContribuyente();

        List<FacturaResponseDto> respuesta =
                facturas.stream()
                        .map(this::convertirFacturaDto)
                        .toList();

        ApiResponse<List<FacturaResponseDto>> response =
                new ApiResponse<>(
                        HttpStatus.OK.value(),
                        respuesta.isEmpty()
                                ? "NO SE ENCONTRARON FACTURAS"
                                : "FACTURAS ENCONTRADAS",
                        respuesta
                );

        return ResponseEntity.ok(response);
    }

    @Transactional
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarFactura(
            @Valid @RequestBody FacturaDto facturaDto
    ) {

        final LocalDateTime ahora = LocalDateTime.now();

        String localDestino = facturaDto.getCodigoLocal();

        String claveAcceso =
                facturaDto.getInfoTributaria().getClaveAcceso();

        /*
         * ============================================================
         * 1. VALIDAR FACTURA DUPLICADA
         * ============================================================
         */
        if (repoFactura.existsByClaveAcceso(claveAcceso)) {

            ApiResponse<ProductoResponse> duplicada =
                    new ApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "LA FACTURA YA FUE REGISTRADA ANTERIORMENTE",
                            null
                    );

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(duplicada);
        }

        /*
         * ============================================================
         * 2. OBTENER / CREAR PROVEEDOR (emisor de la factura)
         * En una factura de COMPRA el contribuyente es quien EMITE
         * (infoTributaria: ruc + razonSocial del proveedor), NO el
         * comprador (infoFactura), que es nuestra propia empresa.
         * El emisor SRI siempre se identifica con RUC (tipo "04").
         * ============================================================
         */
        String identificacionProveedor =
                facturaDto
                        .getInfoTributaria()
                        .getRuc();

        Contribuyente contribuyente =
                repoContribuyente.findByIdentificacion(
                        identificacionProveedor
                ).orElseGet(() -> {

                    Contribuyente nuevoContribuyente =
                            new Contribuyente();

                    nuevoContribuyente.setTipoIdentificacion("04");

                    nuevoContribuyente.setIdentificacion(
                            identificacionProveedor
                    );

                    nuevoContribuyente.setRazonSocial(
                            facturaDto
                                    .getInfoTributaria()
                                    .getRazonSocial()
                    );

                    nuevoContribuyente.setDireccion(
                            facturaDto
                                    .getInfoTributaria()
                                    .getDirMatriz()
                    );

                    return repoContribuyente.save(
                            nuevoContribuyente
                    );
                });

        /*
         * ============================================================
         * 3. CREAR FACTURA
         * ============================================================
         */
        Factura nuevaFactura = new Factura();

        /*
         * El ID debe ser generado antes porque se utiliza como
         * referencia en Producto y ProductoLocal.
         */
        nuevaFactura.setId(
                getSiguienteSecuenciaFactura()
        );

        nuevaFactura.setContribuyente(contribuyente);

        nuevaFactura.setAmbiente(
                facturaDto
                        .getInfoTributaria()
                        .getAmbiente()
        );

        nuevaFactura.setTipoEmision(
                facturaDto
                        .getInfoTributaria()
                        .getTipoEmision()
        );

        nuevaFactura.setRazonSocial(
                facturaDto
                        .getInfoTributaria()
                        .getRazonSocial()
        );

        nuevaFactura.setNombreComercial(
                facturaDto
                        .getInfoTributaria()
                        .getNombreComercial()
        );

        nuevaFactura.setRuc(
                facturaDto
                        .getInfoTributaria()
                        .getRuc()
        );

        nuevaFactura.setClaveAcceso(
                facturaDto
                        .getInfoTributaria()
                        .getClaveAcceso()
        );

        nuevaFactura.setCodDoc(
                facturaDto
                        .getInfoTributaria()
                        .getCodDoc()
        );

        nuevaFactura.setEstab(
                facturaDto
                        .getInfoTributaria()
                        .getEstab()
        );

        nuevaFactura.setPtoEmi(
                facturaDto
                        .getInfoTributaria()
                        .getPtoEmi()
        );

        nuevaFactura.setSecuencial(
                facturaDto
                        .getInfoTributaria()
                        .getSecuencial()
        );

        nuevaFactura.setDirMatriz(
                facturaDto
                        .getInfoTributaria()
                        .getDirMatriz()
        );

        nuevaFactura.setDirEstablecimiento(
                facturaDto
                        .getInfoFactura()
                        .getDirEstablecimiento()
        );

        nuevaFactura.setFechaEmision(
                LocalDate.parse(
                        facturaDto
                                .getInfoFactura()
                                .getFechaEmision(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
        );

        nuevaFactura.setTotalSinImpuestos(
                BigDecimal.valueOf(
                        facturaDto
                                .getInfoFactura()
                                .getTotalSinImpuestos()
                )
        );

        nuevaFactura.setTotalDescuento(
                BigDecimal.valueOf(
                        facturaDto
                                .getInfoFactura()
                                .getTotalDescuento()
                )
        );

        nuevaFactura.setImporteTotal(
                BigDecimal.valueOf(
                        facturaDto
                                .getInfoFactura()
                                .getImporteTotal()
                )
        );

        nuevaFactura.setEstadoSri("AUTORIZADO");

        nuevaFactura.setFechaAutorizacion(ahora);

        nuevaFactura.setNumeroAutorizacion(
                facturaDto
                        .getInfoTributaria()
                        .getClaveAcceso()
        );

        /*
         * ============================================================
         * 4. FORMAS DE PAGO
         * ============================================================
         */
        if (facturaDto.getInfoFactura().getPagos() != null
                && facturaDto
                .getInfoFactura()
                .getPagos()
                .getPago() != null) {

            for (PagoDto pagoDto :
                    facturaDto
                            .getInfoFactura()
                            .getPagos()
                            .getPago()) {

                FormaPago formaPago = new FormaPago();

                formaPago.setFormaPago(
                        pagoDto.getFormaPago()
                );

                formaPago.setTotal(
                        BigDecimal.valueOf(
                                pagoDto.getTotal()
                        )
                );

                formaPago.setPlazo(
                        BigDecimal.valueOf(
                                pagoDto.getPlazo() != null
                                        ? pagoDto.getPlazo()
                                        : 0
                        )
                );

                formaPago.setUnidadTiempo(
                        pagoDto.getUnidadTiempo() != null
                                ? pagoDto.getUnidadTiempo()
                                : "dias"
                );

                nuevaFactura.addFormaPago(
                        formaPago
                );
            }
        }

        /*
         * ============================================================
         * 5. PROCESAR DETALLES
         * ============================================================
         */

        int count = 0;

        for (FacturaDetalleDto prod :
                facturaDto
                        .getDetalles()
                        .getDetalle()) {

            /*
             * --------------------------------------------------------
             * 5.1 Crear detalle de factura
             * --------------------------------------------------------
             */
            FacturaDetalle detalleEntidad =
                    new FacturaDetalle();

            detalleEntidad.setCodigoPrincipal(
                    prod.getCodigoPrincipal()
            );

            detalleEntidad.setCodigoAuxiliar(
                    prod.getCodigoAuxiliar() != null
                            ? prod.getCodigoAuxiliar()
                            : prod.getCodigoPrincipal()
            );

            detalleEntidad.setDescripcion(
                    prod.getDescripcion()
            );

            detalleEntidad.setCantidad(
                    BigDecimal.valueOf(
                            prod.getCantidad()
                    )
            );

            detalleEntidad.setPrecioUnitario(
                    BigDecimal.valueOf(
                            prod.getPrecioUnitario()
                    )
            );

            detalleEntidad.setDescuento(
                    prod.getDescuento() != null
                            ? BigDecimal.valueOf(
                            prod.getDescuento()
                    )
                            : BigDecimal.ZERO
            );

            detalleEntidad.setPrecioTotalSinImpuesto(
                    BigDecimal.valueOf(
                            prod.getPrecioTotalSinImpuesto()
                    )
            );

            /*
             * --------------------------------------------------------
             * 5.2 Impuestos del detalle
             * --------------------------------------------------------
             */
            if (prod.getImpuestos() != null
                    && prod
                    .getImpuestos()
                    .getImpuesto() != null) {

                for (ImpuestoDetalleDto impDto :
                        prod
                                .getImpuestos()
                                .getImpuesto()) {

                    DetalleImpuesto detalleImpuesto =
                            new DetalleImpuesto();

                    detalleImpuesto.setCodigo(
                            impDto.getCodigo()
                    );

                    detalleImpuesto.setCodigoPorcentaje(
                            impDto.getCodigoPorcentaje()
                    );

                    detalleImpuesto.setTarifa(
                            BigDecimal.valueOf(
                                    impDto.getTarifa()
                            )
                    );

                    detalleImpuesto.setBaseInponible(
                            BigDecimal.valueOf(
                                    impDto.getBaseImponible()
                            )
                    );

                    detalleImpuesto.setValor(
                            BigDecimal.valueOf(
                                    impDto.getValor()
                            )
                    );

                    detalleEntidad.addImpuesto(
                            detalleImpuesto
                    );
                }
            }

            nuevaFactura.addDetalle(
                    detalleEntidad
            );

            /*
             * --------------------------------------------------------
             * 5.3 Buscar producto vigente
             * --------------------------------------------------------
             */
            Producto existeProducto =
                    repoProducto.findByCodProductoProveedor(
                            prod.getCodigoPrincipal(),
                            LocalDateTime.of(
                                    2999,
                                    12,
                                    31,
                                    0,
                                    0,
                                    0
                            )
                    );

            /*
             * ========================================================
             * 6. PRODUCTO NUEVO
             * ========================================================
             */
            if (existeProducto == null) {

                Integer nuevaSecuencia =
                        getSiguienteSecuencia();

                /*
                 * ----------------------------------------------------
                 * 6.1 Crear Producto
                 * ----------------------------------------------------
                 */
                ProductoPk pk =
                        new ProductoPk();

                pk.setSecProducto(
                        nuevaSecuencia
                );

                pk.setFechaFin(
                        LocalDateTime.of(
                                2999,
                                12,
                                31,
                                0,
                                0,
                                0
                        )
                );

                Producto nuevoProducto =
                        new Producto();

                nuevoProducto.setId(pk);

                nuevoProducto.setFechaInicio(
                        ahora
                );

                nuevoProducto.setCodProductoProveedor(
                        prod.getCodigoPrincipal()
                );

                nuevoProducto.setDescripcion(
                        prod.getDescripcion()
                );

                nuevoProducto.setPrecioVenta(
                        new BigDecimal(
                                prod.getPrecioVentaPvp()
                        )
                );

                nuevoProducto.setCodEstado(
                        "ACT"
                );

                nuevoProducto.setFechaIngreso(
                        ahora
                );

                nuevoProducto.setCodUsuarioIngreso(
                        "1"
                );

                nuevoProducto.setCodBarra(
                        null
                );

                /*
                 * Factura que originó el producto
                 */
                nuevoProducto.setCodIdFactura(
                        nuevaFactura.getId()
                );

                nuevoProducto =
                        repoProducto.save(
                                nuevoProducto
                        );

                /*
                 * ----------------------------------------------------
                 * 6.2 Crear ProductoLocal
                 * ----------------------------------------------------
                 */
                ProductoLocalPk plPk =
                        new ProductoLocalPk();

                plPk.setSecLocal(
                        Long.parseLong(localDestino)
                );

                plPk.setSecProducto(
                        nuevaSecuencia
                );

                plPk.setFechaFin(
                        LocalDateTime.of(
                                2999,
                                12,
                                31,
                                0,
                                0,
                                0
                        )
                );

                ProductoLocal nuevoProductoLocal =
                        new ProductoLocal();

                nuevoProductoLocal.setId(
                        plPk
                );

                nuevoProductoLocal.setFechaInicio(
                        ahora
                );

                nuevoProductoLocal.setCantidad(
                        prod
                                .getCantidadModificada()
                                .intValue()
                );

                nuevoProductoLocal.setFechaIngreso(
                        ahora
                );

                nuevoProductoLocal.setCodUsuarioIngreso(
                        "1"
                );

                /*
                 * Factura que originó el ingreso de inventario
                 */
                nuevoProductoLocal.setIdFactura(
                        nuevaFactura.getId()
                );

                repoProductoLocal.save(
                        nuevoProductoLocal
                );

                /*
                 * Se procesó correctamente el producto
                 */
                count++;

            } else {

                /*
                 * ====================================================
                 * 7. PRODUCTO EXISTENTE Y ACTIVO
                 * ====================================================
                 */
                if ("ACT".equals(
                        existeProducto.getCodEstado()
                )) {

                    /*
                     * ------------------------------------------------
                     * 7.1 Crear versión histórica de Producto
                     * ------------------------------------------------
                     */
                    Producto histProducto =
                            new Producto();

                    BeanUtils.copyProperties(
                            existeProducto,
                            histProducto,
                            "id"
                    );

                    ProductoPk historialProductoPk =
                            new ProductoPk();

                    historialProductoPk.setSecProducto(
                            existeProducto
                                    .getId()
                                    .getSecProducto()
                    );

                    historialProductoPk.setFechaFin(
                            ahora
                    );

                    histProducto.setId(
                            historialProductoPk
                    );

                    /*
                     * La fecha_inicio es el momento en que se crea
                     * el registro histórico. La fecha_fin (ahora)
                     * ya quedó en el PK y no se modifica.
                     */
                    histProducto.setFechaInicio(
                            ahora
                    );

                    repoProducto.save(
                            histProducto
                    );

                    /*
                     * ------------------------------------------------
                     * 7.2 Actualizar Producto vigente
                     * ------------------------------------------------
                     */
                    existeProducto.setPrecioVenta(
                            new BigDecimal(
                                    prod.getPrecioVentaPvp()
                            )
                    );

                    existeProducto.setCodUsuarioModificacion(
                            "1"
                    );

                    existeProducto.setFechaModificacion(
                            ahora
                    );

                    /*
                     * Nueva factura que actualizó el producto
                     */
                    existeProducto.setCodIdFactura(
                            nuevaFactura.getId()
                    );

                    repoProducto.save(
                            existeProducto
                    );

                    /*
                     * ------------------------------------------------
                     * 7.3 Buscar ProductoLocal vigente
                     * ------------------------------------------------
                     */
                    ProductoLocal existeProductoLocal =
                            repoProductoLocal.findById(
                                    Long.parseLong(localDestino),
                                    existeProducto
                                            .getId()
                                            .getSecProducto(),
                                    LocalDateTime.of(
                                            2999,
                                            12,
                                            31,
                                            0,
                                            0,
                                            0
                                    )
                            );

                    /*
                     * =================================================
                     * 7.4 EL PRODUCTO EXISTE PERO NO EN ESTE LOCAL
                     * =================================================
                     */
                    if (existeProductoLocal == null) {

                        ProductoLocalPk nuevoLocalPk =
                                new ProductoLocalPk();

                        nuevoLocalPk.setSecLocal(
                                Long.parseLong(localDestino)
                        );

                        nuevoLocalPk.setSecProducto(
                                existeProducto
                                        .getId()
                                        .getSecProducto()
                        );

                        nuevoLocalPk.setFechaFin(
                                LocalDateTime.of(
                                        2999,
                                        12,
                                        31,
                                        0,
                                        0,
                                        0
                                )
                        );

                        ProductoLocal nuevoProductoLocal =
                                new ProductoLocal();

                        nuevoProductoLocal.setId(
                                nuevoLocalPk
                        );

                        nuevoProductoLocal.setFechaInicio(
                                ahora
                        );

                        nuevoProductoLocal.setCantidad(
                                prod
                                        .getCantidadModificada()
                                        .intValue()
                        );

                        nuevoProductoLocal.setFechaIngreso(
                                ahora
                        );

                        nuevoProductoLocal.setCodUsuarioIngreso(
                                "1"
                        );

                        /*
                         * Factura que generó el ingreso
                         */
                        nuevoProductoLocal.setIdFactura(
                                nuevaFactura.getId()
                        );

                        repoProductoLocal.save(
                                nuevoProductoLocal
                        );

                    } else {

                        /*
                         * =================================================
                         * 7.5 EL PRODUCTO YA EXISTE EN EL LOCAL
                         * =================================================
                         */

                        /*
                         * ---------------------------------------------
                         * Crear versión histórica de ProductoLocal
                         * ---------------------------------------------
                         */
                        ProductoLocal histProductoLocal =
                                new ProductoLocal();

                        BeanUtils.copyProperties(
                                existeProductoLocal,
                                histProductoLocal,
                                "id"
                        );

                        ProductoLocalPk histProductoLocalPk =
                                new ProductoLocalPk();

                        histProductoLocalPk.setFechaFin(
                                ahora
                        );

                        histProductoLocalPk.setSecLocal(
                                existeProductoLocal
                                        .getId()
                                        .getSecLocal()
                        );

                        histProductoLocalPk.setSecProducto(
                                existeProductoLocal
                                        .getId()
                                        .getSecProducto()
                        );

                        histProductoLocal.setId(
                                histProductoLocalPk
                        );

                        /*
                         * La fecha_inicio es el momento en que se crea
                         * el registro histórico. La fecha_fin (ahora)
                         * ya quedó en el PK y no se modifica.
                         */
                        histProductoLocal.setFechaInicio(
                                ahora
                        );

                        /*
                         * Aquí BeanUtils ya copió el idFactura
                         * correspondiente a la versión anterior.
                         */
                        repoProductoLocal.save(
                                histProductoLocal
                        );

                        /*
                         * ---------------------------------------------
                         * Actualizar stock vigente
                         * ---------------------------------------------
                         */
                        Integer cantidadActual =
                                existeProductoLocal.getCantidad();

                        Integer cantidadNueva =
                                cantidadActual
                                        + prod
                                        .getCantidad()
                                        .intValue();

                        existeProductoLocal.setCantidad(
                                cantidadNueva
                        );

                        /*
                         * La versión vigente queda asociada
                         * a la nueva factura.
                         */
                        existeProductoLocal.setIdFactura(
                                nuevaFactura.getId()
                        );

                        existeProductoLocal.setCodUsuarioModificacion(
                                "1"
                        );

                        existeProductoLocal.setFechaModificacion(
                                ahora
                        );

                        repoProductoLocal.save(
                                existeProductoLocal
                        );
                    }

                    /*
                     * Se procesó correctamente el producto
                     */
                    count++;

                } else {

                    /*
                     * Producto existente pero inactivo.
                     *
                     * No se actualiza inventario.
                     */
                    System.out.println(
                            "Producto INACTIVO - NO actualizar: "
                                    + prod.getCodigoPrincipal()
                    );
                }
            }
        }

        /*
         * ============================================================
         * 8. GUARDAR FACTURA
         * ============================================================
         */
        if (count > 0) {

            repoFactura.save(
                    nuevaFactura
            );

            System.out.println(
                    "Total de productos guardados: "
                            + count
            );

            ApiResponse<ProductoResponse> response =
                    new ApiResponse<>(
                            HttpStatus.CREATED.value(),
                            "FACTURA Y PRODUCTOS REGISTRADOS CORRECTAMENTE",
                            null
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }

        /*
         * ============================================================
         * 9. NINGÚN PRODUCTO FUE PROCESADO
         * ============================================================
         */
        System.out.println(
                "Error al guardar productos de factura"
        );

        ApiResponse<ProductoResponse> response =
                new ApiResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "ERROR AL REGISTRAR LA FACTURA",
                        null
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    public Integer getSiguienteSecuencia() {
        Integer maxActual = repoProducto.getMaxSecuencia();
        return maxActual + 1;
    }

    public Integer getSiguienteSecuenciaFactura() {
        Integer maxActual = repoFactura.getMaxSecuencia();
        return maxActual + 1;
    }

    /*mapear para Dto*/
    private FacturaResponseDto convertirFacturaDto(
            Factura factura
    ) {

        FacturaResponseDto dto =
                new FacturaResponseDto();

        /*
         * ============================================================
         * DATOS DE FACTURA
         * ============================================================
         */

        dto.setId(
                factura.getId()
        );

        dto.setAmbiente(
                factura.getAmbiente()
        );

        dto.setTipoEmision(
                factura.getTipoEmision()
        );

        dto.setRazonSocial(
                factura.getRazonSocial()
        );

        dto.setNombreComercial(
                factura.getNombreComercial()
        );

        dto.setRuc(
                factura.getRuc()
        );

        dto.setClaveAcceso(
                factura.getClaveAcceso()
        );

        dto.setCodDoc(
                factura.getCodDoc()
        );

        dto.setEstab(
                factura.getEstab()
        );

        dto.setPtoEmi(
                factura.getPtoEmi()
        );

        dto.setSecuencial(
                factura.getSecuencial()
        );

        dto.setDirMatriz(
                factura.getDirMatriz()
        );

        dto.setDirEstablecimiento(
                factura.getDirEstablecimiento()
        );

        dto.setFechaEmision(
                factura.getFechaEmision()
        );

        dto.setTotalSinImpuestos(
                factura.getTotalSinImpuestos()
        );

        dto.setTotalDescuento(
                factura.getTotalDescuento()
        );

        dto.setImporteTotal(
                factura.getImporteTotal()
        );

        /*
         * ============================================================
         * CONTROL SRI
         * ============================================================
         */

        dto.setEstadoSri(
                factura.getEstadoSri()
        );

        dto.setFechaAutorizacion(
                factura.getFechaAutorizacion()
        );

        dto.setNumeroAutorizacion(
                factura.getNumeroAutorizacion()
        );

        /*
         * ============================================================
         * CONTRIBUYENTE
         * ============================================================
         */

        if (factura.getContribuyente() != null) {

            Contribuyente contribuyente =
                    factura.getContribuyente();

            ContribuyenteResponseDto contribuyenteDto =
                    new ContribuyenteResponseDto();

            contribuyenteDto.setId(
                    contribuyente.getId()
            );

            contribuyenteDto.setTipoIdentificacion(
                    contribuyente.getTipoIdentificacion()
            );

            contribuyenteDto.setIdentificacion(
                    contribuyente.getIdentificacion()
            );

            contribuyenteDto.setRazonSocial(
                    contribuyente.getRazonSocial()
            );

            contribuyenteDto.setDireccion(
                    contribuyente.getDireccion()
            );

            dto.setContribuyente(
                    contribuyenteDto
            );
        }

        /*
         * ============================================================
         * DETALLES
         * ============================================================
         */

        List<FacturaDetalleResponseDto> detalles =
                factura.getDetalles() == null
                        ? Collections.emptyList()
                        : factura.getDetalles()
                        .stream()
                        .map(this::convertirDetalleDto)
                        .toList();

        dto.setDetalles(
                detalles
        );

        /*
         * ============================================================
         * FORMAS DE PAGO
         * ============================================================
         */

        List<FormaPagoResponseDto> formasPago =
                factura.getFormasPago() == null
                        ? Collections.emptyList()
                        : factura.getFormasPago()
                        .stream()
                        .map(this::convertirFormaPagoDto)
                        .toList();

        dto.setFormasPago(
                formasPago
        );

        return dto;
    }

    private FacturaDetalleResponseDto convertirDetalleDto(
            FacturaDetalle detalle
    ) {

        FacturaDetalleResponseDto dto =
                new FacturaDetalleResponseDto();

        dto.setId(
                detalle.getId()
        );

        dto.setCodigoPrincipal(
                detalle.getCodigoPrincipal()
        );

        dto.setCodigoAuxiliar(
                detalle.getCodigoAuxiliar()
        );

        dto.setDescripcion(
                detalle.getDescripcion()
        );

        dto.setCantidad(
                detalle.getCantidad()
        );

        dto.setPrecioUnitario(
                detalle.getPrecioUnitario()
        );

        dto.setDescuento(
                detalle.getDescuento()
        );

        dto.setPrecioTotalSinImpuesto(
                detalle.getPrecioTotalSinImpuesto()
        );

        List<DetalleImpuestoResponseDto> impuestos =
                detalle.getImpuestos() == null
                        ? Collections.emptyList()
                        : detalle.getImpuestos()
                        .stream()
                        .map(this::convertirImpuestoDto)
                        .toList();

        dto.setImpuestos(
                impuestos
        );

        return dto;
    }

    private DetalleImpuestoResponseDto convertirImpuestoDto(
            DetalleImpuesto impuesto
    ) {

        DetalleImpuestoResponseDto dto =
                new DetalleImpuestoResponseDto();

        dto.setId(
                impuesto.getId()
        );

        dto.setCodigo(
                impuesto.getCodigo()
        );

        dto.setCodigoPorcentaje(
                impuesto.getCodigoPorcentaje()
        );

        dto.setTarifa(
                impuesto.getTarifa()
        );

        dto.setBaseInponible(
                impuesto.getBaseInponible()
        );

        dto.setValor(
                impuesto.getValor()
        );

        return dto;
    }

    private FormaPagoResponseDto convertirFormaPagoDto(
            FormaPago formaPago
    ) {

        FormaPagoResponseDto dto =
                new FormaPagoResponseDto();

        dto.setId(
                formaPago.getId()
        );

        dto.setFormaPago(
                formaPago.getFormaPago()
        );

        dto.setTotal(
                formaPago.getTotal()
        );

        dto.setPlazo(
                formaPago.getPlazo()
        );

        dto.setUnidadTiempo(
                formaPago.getUnidadTiempo()
        );

        return dto;
    }
}
