package com.erp.accounting.repository;

import com.erp.accounting.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    @Query("SELECT b FROM Budget b WHERE :q IS NULL OR LOWER(b.categoryName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(b.period) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Budget> search(@Param("q") String q, Pageable pageable);
}
