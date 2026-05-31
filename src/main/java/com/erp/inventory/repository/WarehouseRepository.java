package com.erp.inventory.repository;

import com.erp.inventory.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    @Query("SELECT w FROM Warehouse w WHERE :q IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(w.location) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Warehouse> search(@Param("q") String q, Pageable pageable);
}
