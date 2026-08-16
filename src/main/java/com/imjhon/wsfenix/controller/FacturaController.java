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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "https://vercel.app")
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

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarFactura(
            @Valid @RequestBody FacturaDto facturaDto
    ) {

        // Ejemplo de lectura de datos aninados y enriquecidos:
        String localDestino = facturaDto.getCodigoLocal();
        String rucEmisor = facturaDto.getInfoTributaria().getRuc();
        Double total = facturaDto.getInfoFactura().getImporteTotal();

        // 0. VALIDACIÓN DE DUPLICADOS: la clave de acceso es única por factura
        String claveAcceso = facturaDto.getInfoTributaria().getClaveAcceso();
        if (repoFactura.existsByClaveAcceso(claveAcceso)) {
            ApiResponse<ProductoResponse> duplicada = new ApiResponse<>(
                    HttpStatus.CONFLICT.value(),
                    "LA FACTURA YA FUE REGISTRADA ANTERIORMENTE",
                    null
            );
            return new ResponseEntity<>(duplicada, HttpStatus.CONFLICT);
        }

        // 1. OBTENER O CREAR EL CONTRIBUYENTE (COMPRADOR)
        Contribuyente contribuyente = repoContribuyente.findByIdentificacion(facturaDto.getInfoFactura().getIdentificacionComprador())
                .orElseGet(() -> {
                    Contribuyente nuevoC = new Contribuyente();
                    nuevoC.setTipoIdentificacion(facturaDto.getInfoFactura().getTipoIdentificacionComprador());
                    nuevoC.setIdentificacion(facturaDto.getInfoFactura().getIdentificacionComprador());
                    nuevoC.setRazonSocial(facturaDto.getInfoFactura().getRazonSocialComprador());
                    nuevoC.setDireccion(facturaDto.getInfoFactura().getDireccionComprador());
                    return repoContribuyente.save(nuevoC);
                });

        // 2. INSTANCIAR Y MAPEAR LA CABECERA GENERAL DE LA FACTURA
        Factura nuevaFactura = new Factura();
        nuevaFactura.setContribuyente(contribuyente);

        // Mapeo InfoTributaria
        nuevaFactura.setAmbiente(facturaDto.getInfoTributaria().getAmbiente());
        nuevaFactura.setTipoEmision(facturaDto.getInfoTributaria().getTipoEmision());
        nuevaFactura.setRazonSocial(facturaDto.getInfoTributaria().getRazonSocial());
        nuevaFactura.setNombreComercial(facturaDto.getInfoTributaria().getNombreComercial());
        nuevaFactura.setRuc(facturaDto.getInfoTributaria().getRuc());
        nuevaFactura.setClaveAcceso(facturaDto.getInfoTributaria().getClaveAcceso());
        nuevaFactura.setCodDoc(facturaDto.getInfoTributaria().getCodDoc());
        nuevaFactura.setEstab(facturaDto.getInfoTributaria().getEstab());
        nuevaFactura.setPtoEmi(facturaDto.getInfoTributaria().getPtoEmi());
        nuevaFactura.setSecuencial(facturaDto.getInfoTributaria().getSecuencial());
        nuevaFactura.setDirMatriz(facturaDto.getInfoTributaria().getDirMatriz());

        // Mapeo InfoFactura Totales
        nuevaFactura.setDirEstablecimiento(facturaDto.getInfoFactura().getDirEstablecimiento());
        nuevaFactura.setFechaEmision(LocalDate.parse(facturaDto.getInfoFactura().getFechaEmision(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        nuevaFactura.setTotalSinImpuestos(BigDecimal.valueOf(facturaDto.getInfoFactura().getTotalSinImpuestos()));
        nuevaFactura.setTotalDescuento(BigDecimal.valueOf(facturaDto.getInfoFactura().getTotalDescuento()));
        nuevaFactura.setImporteTotal(BigDecimal.valueOf(facturaDto.getInfoFactura().getImporteTotal()));

        // Estados por defecto del flujo electrónico del SRI
        nuevaFactura.setEstadoSri("AUTORIZADO");
        nuevaFactura.setFechaAutorizacion(LocalDateTime.now());
        nuevaFactura.setNumeroAutorizacion(facturaDto.getInfoTributaria().getClaveAcceso());

        // 3. MAPEO E INTEGRACIÓN DE LAS FORMAS DE PAGO DE LA FACTURA
        if (facturaDto.getInfoFactura().getPagos() != null && facturaDto.getInfoFactura().getPagos().getPago() != null) {
            for (PagoDto pagoDto : facturaDto.getInfoFactura().getPagos().getPago()) {
                FormaPago fp = new FormaPago();
                fp.setFormaPago(pagoDto.getFormaPago());
                fp.setTotal(BigDecimal.valueOf(pagoDto.getTotal()));
                fp.setPlazo(BigDecimal.valueOf(pagoDto.getPlazo() != null ? pagoDto.getPlazo() : 0));
                fp.setUnidadTiempo(pagoDto.getUnidadTiempo() != null ? pagoDto.getUnidadTiempo() : "dias");
                nuevaFactura.addFormaPago(fp);
            }
        }

        // Iteramos los productos leídos del XML que ya tienen el PVP integrado
        int count = 0;
        for (FacturaDetalleDto prod : facturaDto.getDetalles().getDetalle()) {
            System.out.println("Producto: " + prod.getDescripcion() + " -> PVP Ingresado: $" + prod.getPrecioVentaPvp());

            // 4. CONSTRUCCIÓN DE CADA DETALLE DE COMPRA JUNTO A SUS IMPUESTOS ASOCIADOS
            FacturaDetalle detalleEntidad = new FacturaDetalle();
            detalleEntidad.setCodigoPrincipal(prod.getCodigoPrincipal());
            detalleEntidad.setCodigoAuxiliar(prod.getCodigoAuxiliar() != null ? prod.getCodigoAuxiliar() : prod.getCodigoPrincipal());
            detalleEntidad.setDescripcion(prod.getDescripcion());
            detalleEntidad.setCantidad(BigDecimal.valueOf(prod.getCantidad()));
            detalleEntidad.setPrecioUnitario(BigDecimal.valueOf(prod.getPrecioUnitario()));
            detalleEntidad.setDescuento(prod.getDescuento() != null ? BigDecimal.valueOf(prod.getDescuento()) : new BigDecimal("0.00"));
            detalleEntidad.setPrecioTotalSinImpuesto(BigDecimal.valueOf(prod.getPrecioTotalSinImpuesto()));

            // Desglose de impuestos individuales del ítem (IVA, ICE)
            if (prod.getImpuestos() != null && prod.getImpuestos().getImpuesto() != null) {
                for (ImpuestoDetalleDto impDto : prod.getImpuestos().getImpuesto()) {
                    DetalleImpuesto di = new DetalleImpuesto();
                    di.setCodigo(impDto.getCodigo());
                    di.setCodigoPorcentaje(impDto.getCodigoPorcentaje());
                    di.setTarifa(BigDecimal.valueOf(impDto.getTarifa()));
                    di.setBaseInponible(BigDecimal.valueOf(impDto.getBaseImponible()));
                    di.setValor(BigDecimal.valueOf(impDto.getValor()));
                    detalleEntidad.addImpuesto(di);
                }
            }

            // Añadimos el detalle estructurado a la instancia de la factura madre
            nuevaFactura.addDetalle(detalleEntidad);

            /* --- INICIO DE TU LÓGICA DE INVENTARIO Y STOCK ACTUAL --- */
            Producto existeProducto = repoProducto.findByCodProductoProveedor(
                    prod.getCodigoPrincipal(),
                    LocalDateTime.of(2999, 12, 31, 0, 0, 0)
            );

            ProductoPk pk = new ProductoPk();
            ProductoLocalPk plPk = new ProductoLocalPk();
            if (existeProducto == null) {
                /*Crear en PRODUCTO*/
                Integer nuevaSecuencia = getSiguienteSecuencia();
                pk.setSecProducto(nuevaSecuencia);
                pk.setFechaFin(LocalDateTime.of(2999, 12, 31, 0, 0, 0));

                Producto nuevoProducto = new Producto();
                nuevoProducto.setId(pk);
                nuevoProducto.setFechaInicio(LocalDateTime.now());
                nuevoProducto.setCodProductoProveedor(prod.getCodigoPrincipal());
                nuevoProducto.setDescripcion(prod.getDescripcion());
                nuevoProducto.setPrecioVenta(new BigDecimal(prod.getPrecioVentaPvp()));
                nuevoProducto.setCodEstado("ACT");
                nuevoProducto.setFechaIngreso(LocalDateTime.now());
                nuevoProducto.setCodUsuarioIngreso("1");
                nuevoProducto.setCodBarra(null);
                nuevoProducto = repoProducto.save(nuevoProducto);

                /*Crear en PRODUCTOLOCALES*/
                plPk.setSecLocal(Long.parseLong(localDestino));
                plPk.setSecProducto(nuevaSecuencia);
                plPk.setFechaFin(LocalDateTime.of(2999, 12, 31, 0, 0, 0));
                ProductoLocal nuevoProdLocal = new ProductoLocal();
                nuevoProdLocal.setId(plPk);
                nuevoProdLocal.setFechaInicio(LocalDateTime.now());
                nuevoProdLocal.setCantidad(prod.getCantidad().intValue());
                nuevoProdLocal.setFechaIngreso(LocalDateTime.now());
                nuevoProdLocal.setCodUsuarioIngreso("1");
                repoProductoLocal.save(nuevoProdLocal);

            } else {
                System.out.println("el producto ya existe -- actualizar: " + prod.getCodigoPrincipal());

                if (existeProducto.getCodEstado().equals("ACT")) {
                    /*Caducar y registrar*/
                    Producto histProducto = new Producto();
                    BeanUtils.copyProperties(existeProducto, histProducto, "id");
                    ProductoPk historialProductoPk = new ProductoPk();
                    historialProductoPk.setSecProducto(existeProducto.getId().getSecProducto());
                    historialProductoPk.setFechaFin(LocalDateTime.now());
                    histProducto.setId(historialProductoPk);
                    repoProducto.save(histProducto);

                    existeProducto.setPrecioVenta(new BigDecimal(prod.getPrecioVentaPvp()));
                    existeProducto.setCodUsuarioModificacion("1");
                    existeProducto.setFechaModificacion(LocalDateTime.now());
                    repoProducto.save(existeProducto);

                    ProductoLocal existeProdLocal = repoProductoLocal.findById(
                            Long.parseLong(localDestino),
                            existeProducto.getId().getSecProducto(),
                            LocalDateTime.of(2999, 12, 31, 0, 0, 0)
                    );

                    /*Caducar y registrar*/
                    ProductoLocal histProdLocal = new ProductoLocal();
                    BeanUtils.copyProperties(existeProdLocal, histProdLocal, "id");
                    ProductoLocalPk histProdLocalPk = new ProductoLocalPk();
                    histProdLocalPk.setFechaFin(LocalDateTime.now());
                    histProdLocalPk.setSecLocal(existeProdLocal.getId().getSecLocal());
                    histProdLocalPk.setSecProducto(existeProdLocal.getId().getSecProducto());
                    histProdLocal.setId(histProdLocalPk);
                    repoProductoLocal.saveAndFlush(histProdLocal);

                    // 5. ACTUALIZACIÓN FINAL DE CANTIDADES EN EL LOCAL DEL INVENTARIO
                    Integer cantidad = existeProdLocal.getCantidad() + prod.getCantidad().intValue();
                    existeProdLocal.setCantidad(cantidad);
                    existeProdLocal.setCodUsuarioModificacion("1");
                    existeProdLocal.setFechaModificacion(LocalDateTime.now());
                    repoProductoLocal.save(existeProdLocal);

                } else {
                    System.out.println("Producto INACTIVO - NO actualizar: " + prod.getCodigoPrincipal());
                }
            }
            /* --- FIN DE TU LÓGICA DE INVENTARIO Y STOCK ACTUAL --- */

            System.out.println("Guardando factura en local: " + localDestino);
            System.out.println("Total procesado: $" + total);
            count++;
        }

        // 6. GUARDADO EN CASCADA DE LA FACTURA ELECTRÓNICA COMPLETA
        if (count > 0) {
            repoFactura.save(nuevaFactura);
        }

        // 7. RESPUESTA DE LA API SEGÚN EL RESULTADO DEL PROCESAMIENTO
        ApiResponse<ProductoResponse> response = null;
        if (count > 0) {
            System.out.println("Total de productos guardados: " + count);
            response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "FACTURA Y PRODUCTOS REGISTRADOS CORRECTAMENTE",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            System.out.println("Error al guardar productos de factura ");
            response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "ERROR AL REGISTRAR LA FACTURA",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public Integer getSiguienteSecuencia() {
        Integer maxActual = repoProducto.getMaxSecuencia();
        return maxActual + 1;
    }
}
