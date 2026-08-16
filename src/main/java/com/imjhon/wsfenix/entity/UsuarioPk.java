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
public class UsuarioPk implements Serializable {

    @Column(name = "cod_usuario")
    private String codUsuario;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioPk)) return false;
        UsuarioPk that = (UsuarioPk) o;
        return Objects.equals(getCodUsuario(), that.getCodUsuario()) &&
                Objects.equals(getFechaFin(), that.getFechaFin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCodUsuario(), getFechaFin());
    }
}
