package com.imjhon.wsfenix.dao.venta;

import com.imjhon.wsfenix.entity.venta.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteDao extends JpaRepository<Cliente, String> {
}
