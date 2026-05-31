package com.erp.support.repository;

import com.erp.support.entity.Ticket;
import com.erp.support.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    @Query("SELECT t FROM Ticket t WHERE (:q IS NULL OR LOWER(t.subject) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(t.ticketNumber) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(t.customerName) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:status IS NULL OR t.status = :status)")
    Page<Ticket> search(@Param("q") String q, @Param("status") TicketStatus status, Pageable pageable);

    long countByStatus(TicketStatus status);
}
