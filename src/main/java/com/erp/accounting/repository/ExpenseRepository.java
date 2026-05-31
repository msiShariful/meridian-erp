package com.erp.accounting.repository;

import com.erp.accounting.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT e FROM Expense e WHERE :q IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(e.claimedBy) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Expense> search(@Param("q") String q, Pageable pageable);
}
