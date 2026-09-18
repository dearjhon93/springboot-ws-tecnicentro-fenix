package com.imjhon.wsfenix.entity.venta;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Mapea TABLA: factura_venta_pago.
 * Bloque SRI: infoFactura -> pagos -> pago.
 */
@Getter
@Setter
@Entity
@Table(name = "factura_venta_pago")
public class FacturaVentaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clave_acceso", referencedColumnName = "clave_acceso", nullable = false)
    private FacturaVenta facturaVenta;

    @Column(name = "forma_pago", length = 2, nullable = false)
    private String formaPago; // Ej: '01', '20'

    @Column(name = "total", precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "plazo", precision = 10, scale = 2)
    private BigDecimal plazo;

    @Column(name = "unidad_tiempo", length = 20)
    private String unidadTiempo; // Ej: 'Dias', 'Meses'
}
