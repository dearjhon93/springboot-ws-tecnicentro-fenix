package com.imjhon.wsfenix.dto.factura.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
@Entity
@Table(name = "forma_pago")
public class FormaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @Column(name = "forma_pago")
    private String formaPago;
    private BigDecimal total;
    private BigDecimal plazo;
    @Column(name = "unidad_tiempo")
    private String unidadTiempo;
}
