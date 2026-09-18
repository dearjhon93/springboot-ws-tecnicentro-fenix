package com.imjhon.wsfenix.entity.venta;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Mapea TABLA: clientes
 * PK = identificacionComprador (RUC, Cedula o Pasaporte).
 */
@Getter
@Setter
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @Column(name = "id_cliente", length = 13)
    private String idCliente;

    @Column(name = "tipo_identificacion", length = 2, nullable = false)
    private String tipoIdentificacion; // 04=RUC, 05=Cedula, 06=Pasaporte, 07=Consumidor Final

    @Column(name = "razon_social", length = 300, nullable = false)
    private String razonSocial;

    @Column(name = "direccion", length = 500)
    private String direccion;
}
