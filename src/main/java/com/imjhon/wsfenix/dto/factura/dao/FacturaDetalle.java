package com.imjhon.wsfenix.dto.factura.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "factura_detalle")
public class FacturaDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @Column(name = "codigo_principal")
    private String codigoPrincipal;
    @Column(name = "codigo_auxiliar")
    private String codigoAuxiliar;
    private String descripcion;
    private BigDecimal cantidad;
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    @Column(name = "precio_total_sin_impuesto")
    private BigDecimal precioTotalSinImpuesto;

    // Relación bidireccional con sus impuestos
    @OneToMany(mappedBy = "facturaDetalle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleImpuesto> impuestos = new ArrayList<>();

    public void addImpuesto(DetalleImpuesto impuesto) {
        impuestos.add(impuesto);
        impuesto.setFacturaDetalle(this);
    }
}

