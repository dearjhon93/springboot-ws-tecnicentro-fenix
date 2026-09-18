package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.ProductoDao;
import com.imjhon.wsfenix.dao.ProductoLocalDao;
import com.imjhon.wsfenix.dao.venta.ClienteDao;
import com.imjhon.wsfenix.dao.venta.FacturaVentaDao;
import com.imjhon.wsfenix.dto.factura.PagoDto;
import com.imjhon.wsfenix.dto.venta.CampoAdicionalDto;
import com.imjhon.wsfenix.dto.venta.response.ClienteResponseDto;
import com.imjhon.wsfenix.dto.venta.response.FacturaVentaAdicionalResponseDto;
import com.imjhon.wsfenix.dto.venta.response.FacturaVentaDetalleImpuestoResponseDto;
import com.imjhon.wsfenix.dto.venta.response.FacturaVentaDetalleResponseDto;
import com.imjhon.wsfenix.dto.venta.response.FacturaVentaPagoResponseDto;
import com.imjhon.wsfenix.dto.venta.response.FacturaVentaResponseDto;
import com.imjhon.wsfenix.dto.venta.FacturaVentaDetalleDto;
import com.imjhon.wsfenix.dto.venta.FacturaVentaDetalleImpuestoDto;
import com.imjhon.wsfenix.dto.venta.FacturaVentaDto;
import com.imjhon.wsfenix.entity.Producto;
import com.imjhon.wsfenix.entity.ProductoLocal;
import com.imjhon.wsfenix.entity.ProductoLocalPk;
import com.imjhon.wsfenix.entity.ProductoPk;
import com.imjhon.wsfenix.entity.venta.Cliente;
import com.imjhon.wsfenix.entity.venta.FacturaVenta;
import com.imjhon.wsfenix.entity.venta.FacturaVentaAdicional;
import com.imjhon.wsfenix.entity.venta.FacturaVentaDetalle;
import com.imjhon.wsfenix.entity.venta.FacturaVentaDetalleImpuesto;
import com.imjhon.wsfenix.entity.venta.FacturaVentaPago;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/factura-venta")
public class FacturaVentaController {

    // Marca de vigencia usada en producto y productolocal (igual que en compras)
    private static final LocalDateTime VIGENTE =
            LocalDateTime.of(2999, 12, 31, 0, 0, 0);

    @Autowired
    private ClienteDao repoCliente;

    @Autowired
    private FacturaVentaDao repoFacturaVenta;

    @Autowired
    private ProductoDao repoProducto;

    @Autowired
    private ProductoLocalDao repoProductoLocal;

    @Transactional
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarFacturaVenta(
            @Valid @RequestBody FacturaVentaDto facturaVentaDto
    ) {

        final LocalDateTime ahora = LocalDateTime.now();

        String localOrigen = facturaVentaDto.getCodigoLocal();

        String claveAcceso =
                facturaVentaDto
                        .getInfoTributaria()
                        .getClaveAcceso();

        /*
         * ============================================================
         * 1. VALIDAR FACTURA DUPLICADA
         * ============================================================
         */
        if (repoFacturaVenta.existsByClaveAcceso(claveAcceso)) {

            ApiResponse<Void> duplicada =
                    new ApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "LA FACTURA DE VENTA YA FUE REGISTRADA ANTERIORMENTE",
                            null
                    );

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(duplicada);
        }

        /*
         * ============================================================
         * 2. OBTENER / CREAR CLIENTE (upsert por identificacion)
         * ============================================================
         */
        String idCliente =
                facturaVentaDto
                        .getInfoFactura()
                        .getIdentificacionComprador();

        Cliente cliente = repoCliente.findById(idCliente)
                .orElseGet(() -> {
                    Cliente nuevo = new Cliente();
                    nuevo.setIdCliente(idCliente);
                    return nuevo;
                });

        cliente.setTipoIdentificacion(
                facturaVentaDto
                        .getInfoFactura()
                        .getTipoIdentificacionComprador()
        );

        cliente.setRazonSocial(
                facturaVentaDto
                        .getInfoFactura()
                        .getRazonSocialComprador()
        );

        cliente.setDireccion(
                facturaVentaDto
                        .getInfoFactura()
                        .getDireccionComprador()
        );

        cliente = repoCliente.save(cliente);

        /*
         * ============================================================
         * 3. CREAR CABECERA DE FACTURA DE VENTA (en memoria)
         * ============================================================
         */
        FacturaVenta nuevaFactura = new FacturaVenta();

        nuevaFactura.setClaveAcceso(claveAcceso);

        nuevaFactura.setNumeroAutorizacion(claveAcceso);

        nuevaFactura.setFechaAutorizacion(ahora);

        nuevaFactura.setEstablecimiento(
                facturaVentaDto
                        .getInfoTributaria()
                        .getEstab()
        );

        nuevaFactura.setPuntoEmision(
                facturaVentaDto
                        .getInfoTributaria()
                        .getPtoEmi()
        );

        nuevaFactura.setSecuencial(
                facturaVentaDto
                        .getInfoTributaria()
                        .getSecuencial()
        );

        nuevaFactura.setFechaEmision(
                parseFechaEmision(
                        facturaVentaDto
                                .getInfoFactura()
                                .getFechaEmision()
                )
        );

        nuevaFactura.setCliente(cliente);

        nuevaFactura.setTotalSinImpuestos(
                BigDecimal.valueOf(
                        facturaVentaDto
                                .getInfoFactura()
                                .getTotalSinImpuestos()
                )
        );

        nuevaFactura.setTotalDescuento(
                BigDecimal.valueOf(
                        facturaVentaDto
                                .getInfoFactura()
                                .getTotalDescuento() != null
                                ? facturaVentaDto
                                        .getInfoFactura()
                                        .getTotalDescuento()
                                : 0.0
                )
        );

        nuevaFactura.setImporteTotal(
                BigDecimal.valueOf(
                        facturaVentaDto
                                .getInfoFactura()
                                .getImporteTotal()
                )
        );

        nuevaFactura.setXmlOriginal(
                facturaVentaDto.getXmlOriginal()
        );

        /*
         * ============================================================
         * 4. FORMAS DE PAGO (pagos -> pago)
         * ============================================================
         */
        if (facturaVentaDto.getInfoFactura().getPagos() != null
                && facturaVentaDto
                        .getInfoFactura()
                        .getPagos()
                        .getPago() != null) {

            for (PagoDto pagoDto :
                    facturaVentaDto
                            .getInfoFactura()
                            .getPagos()
                            .getPago()) {

                FacturaVentaPago pago = new FacturaVentaPago();

                pago.setFormaPago(pagoDto.getFormaPago());

                pago.setTotal(
                        BigDecimal.valueOf(pagoDto.getTotal())
                );

                pago.setPlazo(
                        pagoDto.getPlazo() != null
                                ? BigDecimal.valueOf(pagoDto.getPlazo())
                                : null
                );

                pago.setUnidadTiempo(pagoDto.getUnidadTiempo());

                nuevaFactura.addPago(pago);
            }
        }

        /*
         * ============================================================
         * 5. DETALLES + IMPUESTOS (en memoria)
         * ============================================================
         */
        for (FacturaVentaDetalleDto detDto :
                facturaVentaDto
                        .getDetalles()
                        .getDetalle()) {

            FacturaVentaDetalle detalle = new FacturaVentaDetalle();

            detalle.setCodigoPrincipal(detDto.getCodigoPrincipal());

            detalle.setCodigoAuxiliar(
                    detDto.getCodigoAuxiliar() != null
                            ? detDto.getCodigoAuxiliar()
                            : detDto.getCodigoPrincipal()
            );

            detalle.setDescripcion(detDto.getDescripcion());

            detalle.setCantidad(detDto.getCantidad());

            detalle.setPrecioUnitario(detDto.getPrecioUnitario());

            detalle.setDescuento(
                    detDto.getDescuento() != null
                            ? detDto.getDescuento()
                            : BigDecimal.ZERO
            );

            detalle.setPrecioTotalSinImpuesto(
                    detDto.getPrecioTotalSinImpuesto()
            );

            if (detDto.getImpuestos() != null
                    && detDto.getImpuestos().getImpuesto() != null) {

                for (FacturaVentaDetalleImpuestoDto impDto :
                        detDto.getImpuestos().getImpuesto()) {

                    FacturaVentaDetalleImpuesto impuesto =
                            new FacturaVentaDetalleImpuesto();

                    impuesto.setCodigoImpuesto(
                            impDto.getCodigoImpuesto()
                    );

                    impuesto.setCodigoPorcentaje(
                            impDto.getCodigoPorcentaje()
                    );

                    impuesto.setTarifa(impDto.getTarifa());

                    impuesto.setBaseImponible(
                            impDto.getBaseImponible()
                    );

                    impuesto.setValor(impDto.getValor());

                    detalle.addImpuesto(impuesto);
                }
            }

            nuevaFactura.addDetalle(detalle);
        }

        /*
         * ============================================================
         * 6. INFORMACION ADICIONAL (opcional)
         * ============================================================
         */
        if (facturaVentaDto.getInfoAdicional() != null
                && facturaVentaDto
                        .getInfoAdicional()
                        .getCampoAdicional() != null) {

            for (CampoAdicionalDto campoDto :
                    facturaVentaDto
                            .getInfoAdicional()
                            .getCampoAdicional()) {

                FacturaVentaAdicional adicional =
                        new FacturaVentaAdicional();

                adicional.setNombreCampo(campoDto.getNombre());

                adicional.setValor(campoDto.getValor());

                nuevaFactura.addAdicional(adicional);
            }
        }

        /*
         * ============================================================
         * 7. VALIDAR STOCK (solo lectura, antes de escribir nada)
         * Se acumula por producto por si el mismo codigo
         * viene repetido en varias lineas.
         * ============================================================
         */
        Map<Long, Integer> cantidadRequerida = new LinkedHashMap<>();
        Map<Long, Producto> productos = new LinkedHashMap<>();
        Map<Long, ProductoLocal> stocks = new LinkedHashMap<>();
        List<String> errores = new ArrayList<>();

        for (FacturaVentaDetalleDto detDto :
                facturaVentaDto
                        .getDetalles()
                        .getDetalle()) {

            String codigo = detDto.getCodigoPrincipal();

            Producto producto =
                    repoProducto.findByCodProductoProveedor(
                            codigo, VIGENTE
                    );

            if (producto == null) {
                errores.add(
                        "CODIGO " + codigo
                                + ": producto no registrado en el sistema"
                );
                continue;
            }

            if (!"ACT".equals(producto.getCodEstado())) {
                errores.add(
                        "CODIGO " + codigo
                                + ": producto inactivo, no se puede vender"
                );
                continue;
            }

            long secProducto =
                    producto.getId().getSecProducto();

            productos.putIfAbsent(secProducto, producto);

            cantidadRequerida.merge(
                    secProducto,
                    detDto.getCantidad().intValue(),
                    Integer::sum
            );
        }

        for (Map.Entry<Long, Integer> entry :
                cantidadRequerida.entrySet()) {

            long secProducto = entry.getKey();
            int requerido = entry.getValue();

            ProductoLocal stock =
                    repoProductoLocal.findById(
                            Long.parseLong(localOrigen),
                            secProducto,
                            VIGENTE
                    );

            if (stock == null) {
                errores.add(
                        "SEC_PRODUCTO " + secProducto
                                + ": sin stock registrado en el local "
                                + localOrigen
                );
                continue;
            }

            int disponible =
                    stock.getCantidad() != null
                            ? stock.getCantidad()
                            : 0;

            if (disponible < requerido) {
                errores.add(
                        "SEC_PRODUCTO " + secProducto
                                + ": stock insuficiente en el local "
                                + localOrigen
                                + " (disponible " + disponible
                                + ", requerido " + requerido + ")"
                );
                continue;
            }

            stocks.put(secProducto, stock);
        }

        if (!errores.isEmpty()) {

            ApiResponse<List<String>> sinStock =
                    new ApiResponse<>(
                            HttpStatus.UNPROCESSABLE_ENTITY.value(),
                            "LA VENTA NO PUEDE REGISTRARSE POR FALTA DE STOCK",
                            errores
                    );

            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(sinStock);
        }

        /*
         * ============================================================
         * 8. RESTAR STOCK CON HISTORICO (igual que en compras)
         * 8.1 Version historica de Producto (fechaFin = ahora)
         * 8.2 Vigente de Producto solo se audita
         *     (la venta no cambia PVP ni origen de compra)
         * 8.3 Version historica de ProductoLocal
         * 8.4 Vigente de ProductoLocal con cantidad restada
         * ============================================================
         */
        for (Map.Entry<Long, Integer> entry :
                cantidadRequerida.entrySet()) {

            long secProducto = entry.getKey();
            int requerido = entry.getValue();

            Producto vigente = productos.get(secProducto);
            ProductoLocal stockVigente = stocks.get(secProducto);

            /*
             * ---------------------------------------------
             * 8.1 Historico de Producto
             * ---------------------------------------------
             */
            Producto histProducto = new Producto();

            BeanUtils.copyProperties(
                    vigente,
                    histProducto,
                    "id"
            );

            ProductoPk histProductoPk = new ProductoPk();

            histProductoPk.setSecProducto(secProducto);

            histProductoPk.setFechaFin(ahora);

            histProducto.setId(histProductoPk);

            /*
             * La fecha_inicio es el momento en que se crea
             * el registro histórico. La fecha_fin (ahora)
             * ya quedó en el PK y no se modifica.
             */
            histProducto.setFechaInicio(ahora);

            repoProducto.save(histProducto);

            /*
             * ---------------------------------------------
             * 8.2 Auditar Producto vigente
             * ---------------------------------------------
             */
            vigente.setCodUsuarioModificacion("1");

            vigente.setFechaModificacion(ahora);

            repoProducto.save(vigente);

            /*
             * ---------------------------------------------
             * 8.3 Historico de ProductoLocal
             * ---------------------------------------------
             */
            ProductoLocal histStock = new ProductoLocal();

            BeanUtils.copyProperties(
                    stockVigente,
                    histStock,
                    "id"
            );

            ProductoLocalPk histStockPk = new ProductoLocalPk();

            histStockPk.setSecLocal(
                    stockVigente.getId().getSecLocal()
            );

            histStockPk.setSecProducto(secProducto);

            histStockPk.setFechaFin(ahora);

            histStock.setId(histStockPk);

            /*
             * La fecha_inicio es el momento en que se crea
             * el registro histórico. La fecha_fin (ahora)
             * ya quedó en el PK y no se modifica.
             */
            histStock.setFechaInicio(ahora);

            repoProductoLocal.save(histStock);

            /*
             * ---------------------------------------------
             * 8.4 Restar cantidad al stock vigente
             * ---------------------------------------------
             */
            stockVigente.setCantidad(
                    stockVigente.getCantidad() - requerido
            );

            stockVigente.setCodUsuarioModificacion("1");

            stockVigente.setFechaModificacion(ahora);

            repoProductoLocal.save(stockVigente);
        }

        /*
         * ============================================================
         * 9. GUARDAR FACTURA (cascada a detalles, impuestos,
         *    pagos y adicionales)
         * ============================================================
         */
        repoFacturaVenta.save(nuevaFactura);

        ApiResponse<Map<String, String>> response =
                new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "FACTURA DE VENTA Y DESCUENTO DE STOCK REGISTRADOS CORRECTAMENTE",
                        Map.of("claveAcceso", claveAcceso)
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * El SRI emite la fecha como "dd/MM/yyyy".
     * Se acepta tambien ISO ("yyyy-MM-dd") por tolerancia con Angular.
     */
    private LocalDate parseFechaEmision(String fechaEmision) {
        try {
            return LocalDate.parse(
                    fechaEmision,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
            );
        } catch (Exception e) {
            return LocalDate.parse(fechaEmision);
        }
    }

    /*
     * ============================================================
     * LISTAR FACTURAS DE VENTA
     * ============================================================
     */
    @Transactional(readOnly = true)
    @GetMapping("/listar")
    public ResponseEntity<?> listarFacturasVenta() {

        List<FacturaVenta> facturas =
                repoFacturaVenta.findTodasConCliente();

        List<FacturaVentaResponseDto> respuesta =
                facturas.stream()
                        .map(this::convertirVentaDto)
                        .toList();

        ApiResponse<List<FacturaVentaResponseDto>> response =
                new ApiResponse<>(
                        HttpStatus.OK.value(),
                        respuesta.isEmpty()
                                ? "NO SE ENCONTRARON FACTURAS DE VENTA"
                                : "FACTURAS DE VENTA ENCONTRADAS",
                        respuesta
                );

        return ResponseEntity.ok(response);
    }

    private FacturaVentaResponseDto convertirVentaDto(FacturaVenta factura) {

        FacturaVentaResponseDto dto = new FacturaVentaResponseDto();

        dto.setClaveAcceso(factura.getClaveAcceso());
        dto.setNumeroAutorizacion(factura.getNumeroAutorizacion());
        dto.setFechaAutorizacion(factura.getFechaAutorizacion());
        dto.setEstablecimiento(factura.getEstablecimiento());
        dto.setPuntoEmision(factura.getPuntoEmision());
        dto.setSecuencial(factura.getSecuencial());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setTotalSinImpuestos(factura.getTotalSinImpuestos());
        dto.setTotalDescuento(factura.getTotalDescuento());
        dto.setImporteTotal(factura.getImporteTotal());

        if (factura.getCliente() != null) {
            Cliente cliente = factura.getCliente();
            ClienteResponseDto clienteDto = new ClienteResponseDto();
            clienteDto.setIdCliente(cliente.getIdCliente());
            clienteDto.setTipoIdentificacion(cliente.getTipoIdentificacion());
            clienteDto.setRazonSocial(cliente.getRazonSocial());
            clienteDto.setDireccion(cliente.getDireccion());
            dto.setCliente(clienteDto);
        }

        dto.setDetalles(
                factura.getDetalles() == null
                        ? Collections.emptyList()
                        : factura.getDetalles().stream()
                                .map(det -> {
                                    FacturaVentaDetalleResponseDto detDto =
                                            new FacturaVentaDetalleResponseDto();
                                    detDto.setIdDetalle(det.getIdDetalle());
                                    detDto.setCodigoPrincipal(det.getCodigoPrincipal());
                                    detDto.setCodigoAuxiliar(det.getCodigoAuxiliar());
                                    detDto.setDescripcion(det.getDescripcion());
                                    detDto.setCantidad(det.getCantidad());
                                    detDto.setPrecioUnitario(det.getPrecioUnitario());
                                    detDto.setDescuento(det.getDescuento());
                                    detDto.setPrecioTotalSinImpuesto(
                                            det.getPrecioTotalSinImpuesto()
                                    );
                                    detDto.setImpuestos(
                                            det.getImpuestos() == null
                                                    ? Collections.emptyList()
                                                    : det.getImpuestos().stream()
                                                            .map(imp -> {
                                                                FacturaVentaDetalleImpuestoResponseDto impDto =
                                                                        new FacturaVentaDetalleImpuestoResponseDto();
                                                                impDto.setIdDetalleImpuesto(
                                                                        imp.getIdDetalleImpuesto()
                                                                );
                                                                impDto.setCodigoImpuesto(
                                                                        imp.getCodigoImpuesto()
                                                                );
                                                                impDto.setCodigoPorcentaje(
                                                                        imp.getCodigoPorcentaje()
                                                                );
                                                                impDto.setTarifa(imp.getTarifa());
                                                                impDto.setBaseImponible(
                                                                        imp.getBaseImponible()
                                                                );
                                                                impDto.setValor(imp.getValor());
                                                                return impDto;
                                                            })
                                                            .toList()
                                    );
                                    return detDto;
                                })
                                .toList()
        );

        dto.setPagos(
                factura.getPagos() == null
                        ? Collections.emptyList()
                        : factura.getPagos().stream()
                                .map(pago -> {
                                    FacturaVentaPagoResponseDto pagoDto =
                                            new FacturaVentaPagoResponseDto();
                                    pagoDto.setIdPago(pago.getIdPago());
                                    pagoDto.setFormaPago(pago.getFormaPago());
                                    pagoDto.setTotal(pago.getTotal());
                                    pagoDto.setPlazo(pago.getPlazo());
                                    pagoDto.setUnidadTiempo(pago.getUnidadTiempo());
                                    return pagoDto;
                                })
                                .toList()
        );

        dto.setAdicionales(
                factura.getAdicionales() == null
                        ? Collections.emptyList()
                        : factura.getAdicionales().stream()
                                .map(ad -> {
                                    FacturaVentaAdicionalResponseDto adDto =
                                            new FacturaVentaAdicionalResponseDto();
                                    adDto.setIdAdicional(ad.getIdAdicional());
                                    adDto.setNombreCampo(ad.getNombreCampo());
                                    adDto.setValor(ad.getValor());
                                    return adDto;
                                })
                                .toList()
        );

        return dto;
    }
}
