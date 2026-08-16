package com.imjhon.wsfenix.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProductoLocalPk implements Serializable {

    @Column(name = "sec_local")
    private long secLocal;

    @Column(name = "sec_producto")
    private Integer secProducto;

    @Column(name = "fecha_fin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaFin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoLocalPk)) return false;
        ProductoLocalPk that = (ProductoLocalPk) o;
        return Objects.equals(getSecLocal(), that.getSecLocal()) &&
                Objects.equals(getSecProducto(), that.getSecProducto()) &&
                Objects.equals(getFechaFin(), that.getFechaFin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSecLocal(), getSecProducto(), getFechaFin());
    }
}
