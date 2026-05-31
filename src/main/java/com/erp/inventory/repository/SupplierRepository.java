package com.erp.inventory.repository;

import com.erp.inventory.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    @Query("SELECT s FROM Supplier s WHERE :q IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Supplier> search(@Param("q") String q, Pageable pageable);
}
