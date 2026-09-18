package com.imjhon.wsfenix.entity.venta;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapea TABLA: factura_venta (cabecera).
 * Bloques SRI: infoTributaria + infoFactura.
 */
@Getter
@Setter
@Entity
@Table(name = "factura_venta")
public class FacturaVenta {

    @Id
    @Column(name = "clave_acceso", length = 49)
    private String claveAcceso;

    @Column(name = "numero_autorizacion", length = 49)
    private String numeroAutorizacion;

    @Column(name = "fecha_autorizacion")
    private LocalDateTime fechaAutorizacion;

    @Column(name = "establecimiento", length = 3, nullable = false)
    private String establecimiento;

    @Column(name = "punto_emision", length = 3, nullable = false)
    private String puntoEmision;

    @Column(name = "secuencial", length = 9, nullable = false)
    private String secuencial;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "total_sin_impuestos", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalSinImpuestos;

    @Column(name = "total_descuento", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalDescuento;

    @Column(name = "importe_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal importeTotal;

    @Column(name = "xml_original", columnDefinition = "TEXT")
    private String xmlOriginal;

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "facturaVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaVentaDetalle> detalles = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "facturaVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaVentaPago> pagos = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "facturaVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaVentaAdicional> adicionales = new ArrayList<>();

    public void addDetalle(FacturaVentaDetalle detalle) {
        detalles.add(detalle);
        detalle.setFacturaVenta(this);
    }

    public void addPago(FacturaVentaPago pago) {
        pagos.add(pago);
        pago.setFacturaVenta(this);
    }

    public void addAdicional(FacturaVentaAdicional adicional) {
        adicionales.add(adicional);
        adicional.setFacturaVenta(this);
    }
}
