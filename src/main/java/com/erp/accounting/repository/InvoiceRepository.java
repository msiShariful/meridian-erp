package com.erp.accounting.repository;

import com.erp.accounting.entity.Invoice;
import com.erp.accounting.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    @Query("SELECT i FROM Invoice i WHERE (:q IS NULL OR LOWER(i.customerName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:status IS NULL OR i.status = :status)")
    Page<Invoice> search(@Param("q") String q, @Param("status") InvoiceStatus status, Pageable pageable);

    long countByStatus(InvoiceStatus status);
}
