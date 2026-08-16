package com.imjhon.wsfenix.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Rol {

    @EmbeddedId
    private RolPk id;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "cod_estado")
    private String codEstado;

}
