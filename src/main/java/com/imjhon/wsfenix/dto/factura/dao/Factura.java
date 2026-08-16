package com.imjhon.wsfenix.dto.factura.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "factura")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // InfoTributariaDto
    private String ambiente;
    @Column(name = "tipo_emision")
    private String tipoEmision = "1";
    @Column(name = "razon_social")
    private String razonSocial;
    @Column(name = "nombre_comercial")
    private String nombreComercial;
    private String ruc;
    @Column(name = "clave_acceso", unique = true)
    private String claveAcceso;
    @Column(name = "cod_doc")
    private String codDoc = "01";
    private String estab;
    @Column(name = "pto_emi")
    private String ptoEmi;
    private String secuencial;
    @Column(name = "dir_matriz")
    private String dirMatriz;

    // InfoFactura / FacturaDto
    @Column(name = "dir_establecimiento")
    private String dirEstablecimiento;
    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;
    @Column(name = "total_sin_impuestos")
    private BigDecimal totalSinImpuestos;
    @Column(name = "total_descuento")
    private BigDecimal totalDescuento;
    @Column(name = "importe_total")
    private BigDecimal importeTotal;

    // Control SRI
    @Column(name = "estado_sri")
    private String estadoSri = "PENDIENTE";
    @Column(name = "fecha_autorizacion")
    private LocalDateTime fechaAutorizacion;
    @Column(name = "numero_autorizacion")
    private String numeroAutorizacion;

    // El cliente o proveedor de la factura
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "contribuyente_id", nullable = false)
    private Contribuyente contribuyente;

    // Relación bidireccional con Detalles (Cascada Completa)
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaDetalle> detalles = new ArrayList<>();

    // Relación bidireccional con Formas de Pago
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormaPago> formasPago = new ArrayList<>();

    public void addDetalle(FacturaDetalle detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
    }

    public void addFormaPago(FormaPago pago) {
        formasPago.add(pago);
        pago.setFactura(this);
    }
}

