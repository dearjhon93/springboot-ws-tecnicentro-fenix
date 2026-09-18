package com.imjhon.wsfenix.entity.venta;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Mapea TABLA: factura_venta_detalle_impuesto.
 * Nodo SRI: detalle -> impuestos -> impuesto.
 */
@Getter
@Setter
@Entity
@Table(name = "factura_venta_detalle_impuesto")
public class FacturaVentaDetalleImpuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_impuesto")
    private Long idDetalleImpuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle", referencedColumnName = "id_detalle", nullable = false)
    private FacturaVentaDetalle detalle;

    @Column(name = "codigo_impuesto", length = 2, nullable = false)
    private String codigoImpuesto; // 2=IVA, 3=ICE, 5=IRBPNR

    @Column(name = "codigo_porcentaje", length = 4, nullable = false)
    private String codigoPorcentaje; // 0=0%, 2=12%, 4=15%, etc.

    @Column(name = "tarifa", precision = 5, scale = 2, nullable = false)
    private BigDecimal tarifa;

    @Column(name = "base_imponible", precision = 12, scale = 2, nullable = false)
    private BigDecimal baseImponible;

    @Column(name = "valor", precision = 12, scale = 2, nullable = false)
    private BigDecimal valor;
}
