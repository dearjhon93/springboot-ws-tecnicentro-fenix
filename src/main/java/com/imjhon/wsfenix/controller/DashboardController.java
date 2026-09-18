package com.imjhon.wsfenix.controller;

import com.imjhon.wsfenix.dao.FacturaDao;
import com.imjhon.wsfenix.dao.LocalDao;
import com.imjhon.wsfenix.dao.ProductoDao;
import com.imjhon.wsfenix.dao.ProductoLocalDao;
import com.imjhon.wsfenix.dao.ProveedorDao;
import com.imjhon.wsfenix.dao.UsuarioDao;
import com.imjhon.wsfenix.dao.venta.ClienteDao;
import com.imjhon.wsfenix.dao.venta.FacturaVentaDao;
import com.imjhon.wsfenix.dto.DashboardResumenDto;
import com.imjhon.wsfenix.dto.ProductoStockBajoDto;
import com.imjhon.wsfenix.dto.TopClienteDto;
import com.imjhon.wsfenix.dto.UltimaVentaDto;
import com.imjhon.wsfenix.dto.VentaMensualDto;
import com.imjhon.wsfenix.entity.venta.FacturaVenta;
import com.imjhon.wsfenix.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    // Stock vigente con cantidad menor o igual se considera bajo
    private static final int UMBRAL_STOCK_BAJO = 5;

    @Autowired
    private ProductoDao repoProducto;

    @Autowired
    private ProductoLocalDao repoProductoLocal;

    @Autowired
    private ProveedorDao repoProveedor;

    @Autowired
    private LocalDao repoLocal;

    @Autowired
    private UsuarioDao repoUsuario;

    @Autowired
    private FacturaDao repoFactura;

    @Autowired
    private FacturaVentaDao repoFacturaVenta;

    @Autowired
    private ClienteDao repoCliente;

    @GetMapping("/resumen")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getResumen() {
        DashboardResumenDto resumen = new DashboardResumenDto();
        resumen.setTotalProductos(repoProducto.countProductosActivos());
        resumen.setTotalProveedores(repoProveedor.count());
        resumen.setTotalLocales(repoLocal.count());
        resumen.setTotalUsuarios(repoUsuario.count());
        resumen.setTotalFacturas(repoFactura.count());
        resumen.setVentasTotales(repoFactura.sumImporteTotal());

        /*
         * ============================================================
         * BLOQUE DE VENTAS (factura_venta)
         * ============================================================
         */
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        long cantidadVentas = repoFacturaVenta.count();
        BigDecimal totalVentas = repoFacturaVenta.sumImporteTotal();

        resumen.setCantidadFacturasVenta(cantidadVentas);
        resumen.setTotalVentas(totalVentas);
        resumen.setVentasDelMes(
                repoFacturaVenta.sumImporteTotalEntre(inicioMes, hoy)
        );
        resumen.setTicketPromedioVenta(
                cantidadVentas > 0
                        ? totalVentas.divide(
                                BigDecimal.valueOf(cantidadVentas),
                                2,
                                RoundingMode.HALF_UP
                        )
                        : BigDecimal.ZERO
        );
        resumen.setVentasUltimos6Meses(ventasUltimos6Meses(hoy));

        /*
         * ============================================================
         * BLOQUE DE COMPRAS (facturas de proveedor)
         * ============================================================
         */
        resumen.setTotalCompras(repoFactura.sumImporteTotal());
        resumen.setComprasDelMes(
                repoFactura.sumImporteTotalEntre(inicioMes, hoy)
        );

        /*
         * ============================================================
         * BLOQUE DE INVENTARIO Y CLIENTES
         * ============================================================
         */
        resumen.setTotalClientes(repoCliente.count());
        resumen.setProductosInactivos(repoProducto.countProductosInactivos());
        resumen.setStockBajo(
                repoProductoLocal.countStockBajo(UMBRAL_STOCK_BAJO)
        );

        /*
         * ============================================================
         * BLOQUE V2: comparativas, series y tablas (aditivo, no rompe v1)
         * ============================================================
         */
        YearMonth mesAnterior = YearMonth.from(hoy).minusMonths(1);

        long cantidadCompras = repoFactura.count();
        BigDecimal totalCompras = nvl(repoFactura.sumImporteTotal());

        BigDecimal ventasMesAnterior = nvl(repoFacturaVenta.sumImporteTotalEntre(
                mesAnterior.atDay(1), mesAnterior.atEndOfMonth()));
        BigDecimal comprasMesAnterior = nvl(repoFactura.sumImporteTotalEntre(
                mesAnterior.atDay(1), mesAnterior.atEndOfMonth()));

        BigDecimal ventasHoy = nvl(repoFacturaVenta.sumImporteTotalEntre(hoy, hoy));
        BigDecimal comprasHoy = nvl(repoFactura.sumImporteTotalEntre(hoy, hoy));

        long cantidadVentasMes = repoFacturaVenta.countEntre(inicioMes, hoy);
        long cantidadComprasMes = repoFactura.countEntre(inicioMes, hoy);

        BigDecimal ventasMes = nvl(resumen.getVentasDelMes());
        BigDecimal comprasMes = nvl(resumen.getComprasDelMes());

        resumen.setCantidadFacturasCompra(cantidadCompras);
        resumen.setCantidadVentasMes(cantidadVentasMes);
        resumen.setCantidadComprasMes(cantidadComprasMes);
        resumen.setVentasHoy(ventasHoy);
        resumen.setComprasHoy(comprasHoy);
        resumen.setVentasMesAnterior(ventasMesAnterior);
        resumen.setComprasMesAnterior(comprasMesAnterior);
        resumen.setCrecimientoVentasPct(crecimientoPct(ventasMes, ventasMesAnterior));
        resumen.setCrecimientoComprasPct(crecimientoPct(comprasMes, comprasMesAnterior));
        resumen.setBalanceMes(ventasMes.subtract(comprasMes));
        resumen.setTicketPromedioCompra(
                cantidadCompras > 0
                        ? totalCompras.divide(
                                BigDecimal.valueOf(cantidadCompras),
                                2,
                                RoundingMode.HALF_UP
                        )
                        : BigDecimal.ZERO
        );
        resumen.setComprasUltimos6Meses(comprasUltimos6Meses(hoy));
        resumen.setVentasUltimos7Dias(ventasUltimos7Dias(hoy));
        resumen.setUltimasVentas(ultimasVentas());
        resumen.setTopClientes(topClientes());
        resumen.setStockBajoDetalle(stockBajoDetalle());

        ApiResponse<DashboardResumenDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "RESUMEN DEL DASHBOARD OBTENIDO",
                resumen);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Serie de los ultimos 6 meses (incluye el actual) para graficos.
     * Los meses sin ventas se rellenan con cero.
     */
    private List<VentaMensualDto> ventasUltimos6Meses(LocalDate hoy) {

        YearMonth mesActual = YearMonth.from(hoy);
        YearMonth mesInicial = mesActual.minusMonths(5);

        Map<String, BigDecimal> totalesPorMes = new HashMap<>();

        List<Object[]> filas =
                repoFacturaVenta.ventasPorMes(mesInicial.atDay(1));

        for (Object[] fila : filas) {
            int anio = ((Number) fila[0]).intValue();
            int mes = ((Number) fila[1]).intValue();
            BigDecimal total = (BigDecimal) fila[2];

            totalesPorMes.put(
                    String.format("%04d-%02d", anio, mes),
                    nvl(total)
            );
        }

        List<VentaMensualDto> serie = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            YearMonth mes = mesInicial.plusMonths(i);
            String periodo = mes.toString(); // "YYYY-MM"

            serie.add(
                    new VentaMensualDto(
                            periodo,
                            totalesPorMes.getOrDefault(
                                    periodo, BigDecimal.ZERO
                            )
                    )
            );
        }

        return serie;
    }

    /**
     * Serie de compras de los ultimos 6 meses (incluye el actual).
     * Los meses sin compras se rellenan con cero.
     */
    private List<VentaMensualDto> comprasUltimos6Meses(LocalDate hoy) {

        YearMonth mesActual = YearMonth.from(hoy);
        YearMonth mesInicial = mesActual.minusMonths(5);

        Map<String, BigDecimal> totalesPorMes = new HashMap<>();

        List<Object[]> filas =
                repoFactura.comprasPorMes(mesInicial.atDay(1));

        for (Object[] fila : filas) {
            int anio = ((Number) fila[0]).intValue();
            int mes = ((Number) fila[1]).intValue();
            BigDecimal total = (BigDecimal) fila[2];

            totalesPorMes.put(
                    String.format("%04d-%02d", anio, mes),
                    nvl(total)
            );
        }

        List<VentaMensualDto> serie = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            YearMonth mes = mesInicial.plusMonths(i);
            String periodo = mes.toString(); // "YYYY-MM"

            serie.add(
                    new VentaMensualDto(
                            periodo,
                            totalesPorMes.getOrDefault(
                                    periodo, BigDecimal.ZERO
                            )
                    )
            );
        }

        return serie;
    }

    /**
     * Serie diaria de ventas de los ultimos 7 dias (incluye hoy).
     * Periodo en formato "YYYY-MM-DD". Los dias sin ventas van en cero.
     */
    private List<VentaMensualDto> ventasUltimos7Dias(LocalDate hoy) {

        LocalDate desde = hoy.minusDays(6);

        Map<String, BigDecimal> totalesPorDia = new HashMap<>();

        for (Object[] fila : repoFacturaVenta.ventasPorDia(desde)) {
            LocalDate fecha = (LocalDate) fila[0];
            BigDecimal total = (BigDecimal) fila[1];
            totalesPorDia.put(fecha.toString(), nvl(total));
        }

        List<VentaMensualDto> serie = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate dia = desde.plusDays(i);
            serie.add(
                    new VentaMensualDto(
                            dia.toString(),
                            totalesPorDia.getOrDefault(
                                    dia.toString(), BigDecimal.ZERO
                            )
                    )
            );
        }

        return serie;
    }

    private List<UltimaVentaDto> ultimasVentas() {

        List<UltimaVentaDto> lista = new ArrayList<>();

        for (FacturaVenta f : repoFacturaVenta.findTop5ByOrderByFechaEmisionDesc()) {
            lista.add(
                    new UltimaVentaDto(
                            f.getClaveAcceso(),
                            f.getEstablecimiento() + "-"
                                    + f.getPuntoEmision() + "-"
                                    + f.getSecuencial(),
                            f.getFechaEmision(),
                            f.getCliente() != null
                                    ? f.getCliente().getRazonSocial()
                                    : "—",
                            nvl(f.getImporteTotal())
                    )
            );
        }

        return lista;
    }

    private List<TopClienteDto> topClientes() {

        List<TopClienteDto> lista = new ArrayList<>();

        for (Object[] fila : repoFacturaVenta.topClientes(PageRequest.of(0, 5))) {
            lista.add(
                    new TopClienteDto(
                            (String) fila[0],
                            (String) fila[1],
                            nvl((BigDecimal) fila[2]),
                            ((Number) fila[3]).longValue()
                    )
            );
        }

        return lista;
    }

    private List<ProductoStockBajoDto> stockBajoDetalle() {

        List<ProductoStockBajoDto> lista = new ArrayList<>();

        for (Object[] fila : repoProductoLocal.stockBajoDetalle(
                UMBRAL_STOCK_BAJO, PageRequest.of(0, 10))) {
            lista.add(
                    new ProductoStockBajoDto(
                            ((Number) fila[0]).longValue(),
                            (String) fila[1],
                            fila[2] != null ? ((Number) fila[2]).intValue() : 0,
                            fila[3] != null ? ((Number) fila[3]).longValue() : null
                    )
            );
        }

        return lista;
    }

    private BigDecimal nvl(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    /**
     * Crecimiento % del mes actual vs mes anterior.
     * Si no habia base previa: 100.0 cuando hay movimiento, 0.0 si no hay.
     */
    private Double crecimientoPct(BigDecimal actual, BigDecimal anterior) {
        BigDecimal act = nvl(actual);
        BigDecimal ant = nvl(anterior);

        if (ant.compareTo(BigDecimal.ZERO) == 0) {
            return act.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }

        return act.subtract(ant)
                .divide(ant, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
