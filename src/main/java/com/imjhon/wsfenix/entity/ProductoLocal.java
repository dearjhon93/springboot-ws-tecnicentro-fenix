package com.imjhon.wsfenix.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "productolocales")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProductoLocal {

    @EmbeddedId
    private ProductoLocalPk id;

    @Column(name = "fecha_inicio")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicio;

    @Column(name = "cantidad")
    private Integer cantidad;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;

    @Column(name = "cod_usuario_ingreso")
    private String codUsuarioIngreso;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "cod_usuario_modificacion")
    private String codUsuarioModificacion;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "sec_producto", referencedColumnName = "sec_producto", insertable = false, updatable = false),
            @JoinColumn(name = "fecha_fin", referencedColumnName = "fecha_fin", insertable = false, updatable = false)
    })
    @JsonBackReference
    private Producto producto;

}
