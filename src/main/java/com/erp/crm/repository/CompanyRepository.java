package com.erp.crm.repository;

import com.erp.crm.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    @Query("SELECT c FROM Company c WHERE :q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(c.industry) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Company> search(@Param("q") String q, Pageable pageable);
}
