package com.erp.procurement.repository;

import com.erp.procurement.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    @Query("SELECT v FROM Vendor v WHERE :q IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(v.contactPerson) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Vendor> search(@Param("q") String q, Pageable pageable);

    List<Vendor> findAllByOrderByNameAsc();
}
