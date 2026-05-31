package com.erp.procurement.repository;

import com.erp.procurement.entity.Requisition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RequisitionRepository extends JpaRepository<Requisition, UUID> {
    @Query("SELECT r FROM Requisition r WHERE :q IS NULL OR LOWER(r.reqNumber) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(r.department) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(r.requestedBy) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Requisition> search(@Param("q") String q, Pageable pageable);
}
