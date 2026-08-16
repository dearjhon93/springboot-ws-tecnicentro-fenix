package com.imjhon.wsfenix.dto.factura.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "contribuyente")
public class Contribuyente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tipo de identificación es obligatorio")
    @Size(max = 2, message = "El tipo de identificación no puede superar los 2 caracteres")
    @Column(name = "tipo_identificacion", length = 2, nullable = false)
    private String tipoIdentificacion; // 04: RUC, 05: Cédula, 06: Pasaporte

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 20, message = "La identificación no puede superar los 20 caracteres")
    @Column(name = "identificacion", length = 20, nullable = false, unique = true)
    private String identificacion;

    @NotBlank(message = "La razón social o nombre es obligatorio")
    @Size(max = 300, message = "La razón social no puede superar los 300 caracteres")
    @Column(name = "razon_social", length = 300, nullable = false)
    private String razonSocial;

    @Size(max = 300, message = "La dirección no puede superar los 300 caracteres")
    @Column(name = "direccion", length = 300)
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    @Column(name = "email", length = 100)
    private String email;
}
