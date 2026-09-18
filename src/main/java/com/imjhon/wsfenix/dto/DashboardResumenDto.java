package com.imjhon.wsfenix.dto;

import com.imjhon.wsfenix.dto.VentaMensualDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenDto {

    private long totalProductos;
    private long totalProveedores;
    private long totalLocales;
    private long totalUsuarios;
    private long totalFacturas;

    // Suma de facturas de COMPRA (proveedores). Se mantiene por
    // compatibilidad con el frontend actual.
    private BigDecimal ventasTotales;

    // Bloque de ventas (tabla factura_venta)
    private long cantidadFacturasVenta;
    private BigDecimal totalVentas;
    private BigDecimal ventasDelMes;
    private BigDecimal ticketPromedioVenta;
    private List<VentaMensualDto> ventasUltimos6Meses = new ArrayList<>();

    // Bloque de compras (tabla factura de proveedores)
    private BigDecimal totalCompras;
    private BigDecimal comprasDelMes;

    // Bloque de inventario y clientes
    private long totalClientes;
    private long productosInactivos;
    private long stockBajo;

    // ---- Nuevos campos (v2, compatibles: todo nullable/aditivo) ----

    // Conteos
    private long cantidadFacturasCompra;
    private long cantidadVentasMes;
    private long cantidadComprasMes;

    // Totales del dia
    private BigDecimal ventasHoy;
    private BigDecimal comprasHoy;

    // Mes anterior + crecimiento % vs mes actual
    private BigDecimal ventasMesAnterior;
    private BigDecimal comprasMesAnterior;
    private Double crecimientoVentasPct;
    private Double crecimientoComprasPct;

    // Balance y ticket de compra
    private BigDecimal balanceMes;
    private BigDecimal ticketPromedioCompra;

    // Series para graficos
    private List<VentaMensualDto> comprasUltimos6Meses = new ArrayList<>();
    // Periodo en formato "YYYY-MM-DD" (ultimos 7 dias, incluye hoy)
    private List<VentaMensualDto> ventasUltimos7Dias = new ArrayList<>();

    // Tablas del dashboard
    private List<UltimaVentaDto> ultimasVentas = new ArrayList<>();
    private List<TopClienteDto> topClientes = new ArrayList<>();
    private List<ProductoStockBajoDto> stockBajoDetalle = new ArrayList<>();

}
