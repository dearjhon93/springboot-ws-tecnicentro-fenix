package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.dto.factura.dao.Contribuyente;
import com.imjhon.wsfenix.dto.factura.dao.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContribuyenteDao extends JpaRepository<Contribuyente,Long> {

    // Método clave utilizado para buscar si el cliente ya existe por RUC/Cédula antes de crearlo
    Optional<Contribuyente> findByIdentificacion(String identificacion);
}
