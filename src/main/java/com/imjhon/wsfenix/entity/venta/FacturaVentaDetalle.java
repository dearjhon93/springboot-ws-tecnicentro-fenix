package com.imjhon.wsfenix.entity.venta;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapea TABLA: factura_venta_detalle.
 * Nodo SRI: detalles -> detalle.
 */
@Getter
@Setter
@Entity
@Table(name = "factura_venta_detalle")
public class FacturaVentaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clave_acceso", referencedColumnName = "clave_acceso", nullable = false)
    private FacturaVenta facturaVenta;

    @Column(name = "codigo_principal", length = 50, nullable = false)
    private String codigoPrincipal;

    @Column(name = "codigo_auxiliar", length = 50)
    private String codigoAuxiliar;

    @Column(name = "descripcion", length = 300, nullable = false)
    private String descripcion;

    @Column(name = "cantidad", precision = 14, scale = 4, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", precision = 18, scale = 6, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "descuento", precision = 12, scale = 2, nullable = false)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "precio_total_sin_impuesto", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioTotalSinImpuesto;

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "detalle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaVentaDetalleImpuesto> impuestos = new ArrayList<>();

    public void addImpuesto(FacturaVentaDetalleImpuesto impuesto) {
        impuestos.add(impuesto);
        impuesto.setDetalle(this);
    }
}
