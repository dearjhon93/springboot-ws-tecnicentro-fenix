package com.imjhon.wsfenix.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "locales")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Local {

    @Id
    @Column(name = "sec_local")
    private long secLocal;

    @Column(name = "nombre_local")
    private String nombreLocal;

    @Column(name = "cod_estado")
    private String cod_estado;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_inicio_actividades")
    private Date fechaInicioActividades;
}
