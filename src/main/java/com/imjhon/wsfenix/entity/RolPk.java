package com.imjhon.wsfenix.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@Data
@NoArgsConstructor
public class RolPk implements Serializable {

    @Column(name = "cod_rol")
    private Integer codRol;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolPk)) return false;
        RolPk that = (RolPk) o;
        return Objects.equals(getCodRol(), that.getCodRol()) &&
                Objects.equals(getFechaFin(), that.getFechaFin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCodRol(), getFechaFin());
    }
}
