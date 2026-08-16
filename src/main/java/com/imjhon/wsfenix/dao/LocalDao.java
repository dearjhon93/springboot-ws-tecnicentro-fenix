package com.imjhon.wsfenix.dao;

import com.imjhon.wsfenix.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalDao extends JpaRepository<Local,Long> {

    @Query("SELECT p FROM Local p WHERE p.secLocal = :id")
    Local findByCodId(Long id);

    @Query("SELECT COALESCE(MAX(l.secLocal), 0) FROM Local l")
    Long getMaxSecuencia();
}
