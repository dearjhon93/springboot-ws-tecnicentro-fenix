package com.imjhon.wsfenix.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tiposempaques")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TipoEmpaque {

    @Id
    @Column(name = "cod_tipoempaque")
    private long codTipoEmpaque;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "cod_estado")
    private String codEstado;
}
