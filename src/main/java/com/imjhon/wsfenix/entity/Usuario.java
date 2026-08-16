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
@Table(name = "usuarios")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Usuario {

    @EmbeddedId
    private UsuarioPk id;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "alias")
    private String alias;

    @Column(name = "clave")
    private String clave;

    @Column(name = "cod_rol")
    private String codRol;

    @Column(name = "cod_estado")
    private String codEstado;

    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;

    @Column(name = "cod_usuario_ingreso")
    private String codUsuarioIngreso;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "cod_usuario_modificacion")
    private String codUsuarioModificacion;

}
