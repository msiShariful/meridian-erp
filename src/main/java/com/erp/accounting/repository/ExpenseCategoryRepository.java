package com.erp.accounting.repository;

import com.erp.accounting.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, UUID> {
}
