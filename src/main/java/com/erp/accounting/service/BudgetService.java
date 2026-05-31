package com.erp.accounting.service;

import com.erp.accounting.entity.Budget;
import com.erp.accounting.repository.BudgetRepository;
import com.erp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;

    @Transactional(readOnly = true)
    public Page<Budget> search(String q, Pageable pageable) {
        return budgetRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public Budget get(UUID id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Budget", id));
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void delete(UUID id) {
        budgetRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
