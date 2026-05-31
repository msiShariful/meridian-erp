package com.erp.accounting.service;

import com.erp.accounting.entity.Expense;
import com.erp.accounting.entity.ExpenseCategory;
import com.erp.accounting.enums.ExpenseStatus;
import com.erp.accounting.repository.ExpenseCategoryRepository;
import com.erp.accounting.repository.ExpenseRepository;
import com.erp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<Expense> search(String q, Pageable pageable) {
        return expenseRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public Expense get(UUID id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Expense", id));
    }

    @Transactional(readOnly = true)
    public List<ExpenseCategory> categories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ExpenseCategory category(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("ExpenseCategory", id));
    }

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void updateStatus(UUID id, ExpenseStatus status) {
        Expense expense = get(id);
        expense.setStatus(status);
        expenseRepository.save(expense);
    }

    public void delete(UUID id) {
        expenseRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
