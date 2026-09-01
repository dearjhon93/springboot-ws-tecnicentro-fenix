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
public class ProductoPk implements Serializable {

    @Column(name = "sec_producto")
    private long secProducto;

    @Column(name = "fecha_fin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime fechaFin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoPk)) return false;
        ProductoPk that = (ProductoPk) o;
        return Objects.equals(getSecProducto(), that.getSecProducto()) &&
                Objects.equals(getFechaFin(), that.getFechaFin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSecProducto(), getFechaFin());
    }
}
