package com.erp.crm.repository;

import com.erp.crm.entity.Lead;
import com.erp.crm.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
    @Query("SELECT l FROM Lead l WHERE (:q IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(l.companyName) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:status IS NULL OR l.status = :status)")
    Page<Lead> search(@Param("q") String q, @Param("status") LeadStatus status, Pageable pageable);

    List<Lead> findByStatus(LeadStatus status);
    long countByStatus(LeadStatus status);
}
