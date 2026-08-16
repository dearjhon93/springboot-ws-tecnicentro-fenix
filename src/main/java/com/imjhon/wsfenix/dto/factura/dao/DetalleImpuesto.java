package com.imjhon.wsfenix.dto.factura.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
@Entity
@Table(name = "detalle_impuesto")
public class DetalleImpuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_detalle_id", nullable = false)
    private FacturaDetalle facturaDetalle;

    private Integer codigo;
    @Column(name = "codigo_porcentaje")
    private Integer codigoPorcentaje;
    private BigDecimal tarifa;
    @Column(name = "base_imponible")
    private BigDecimal baseInponible;
    private BigDecimal valor;
}

