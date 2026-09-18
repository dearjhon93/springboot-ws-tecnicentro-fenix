package com.imjhon.wsfenix.entity.venta;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Mapea TABLA: factura_venta_adicional.
 * Bloque SRI: infoAdicional -> campoAdicional.
 */
@Getter
@Setter
@Entity
@Table(name = "factura_venta_adicional")
public class FacturaVentaAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adicional")
    private Long idAdicional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clave_acceso", referencedColumnName = "clave_acceso", nullable = false)
    private FacturaVenta facturaVenta;

    @Column(name = "nombre_campo", length = 100, nullable = false)
    private String nombreCampo; // Ej: 'Email', 'DireccionCliente'

    @Column(name = "valor", length = 300, nullable = false)
    private String valor;
}
