package com.erp.accounting.repository;

import com.erp.accounting.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT p FROM Payment p WHERE :q IS NULL OR LOWER(p.reference) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(p.invoice.invoiceNumber) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Payment> search(@Param("q") String q, Pageable pageable);

    List<Payment> findByInvoiceId(UUID invoiceId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.invoice.id = :invoiceId")
    BigDecimal sumByInvoiceId(@Param("invoiceId") UUID invoiceId);
}
