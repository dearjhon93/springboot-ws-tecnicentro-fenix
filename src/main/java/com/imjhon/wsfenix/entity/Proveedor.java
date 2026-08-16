package com.imjhon.wsfenix.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proveedores")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Proveedor {

    @Id
    @Column(name = "sec_proveedor")
    private long secProveedor;

    @Column(name = "nombre_proveedor")
    private String nombreProveedor;

    @Column(name = "dir_email")
    private String dirEmail;

    @Column(name = "num_telefono")
    private String numTelefono;

    @Column(name = "num_celular")
    private String numCelular;

    @Column(name = "cod_estado")
    private String cod_estado;
}
