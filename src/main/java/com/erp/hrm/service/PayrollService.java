package com.erp.hrm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.hrm.entity.Payroll;
import com.erp.hrm.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;

    @Transactional(readOnly = true)
    public Page<Payroll> list(Pageable pageable) {
        return payrollRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Payroll get(UUID id) {
        return payrollRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Payroll", id));
    }

    @Transactional
    public Payroll save(Payroll payroll) {
        BigDecimal basic = payroll.getBasicSalary() != null ? payroll.getBasicSalary() : BigDecimal.ZERO;
        BigDecimal allowances = payroll.getAllowances() != null ? payroll.getAllowances() : BigDecimal.ZERO;
        BigDecimal deductions = payroll.getDeductions() != null ? payroll.getDeductions() : BigDecimal.ZERO;
        payroll.setNetSalary(basic.add(allowances).subtract(deductions));
        return payrollRepository.save(payroll);
    }

    @Transactional
    public void delete(UUID id) {
        payrollRepository.deleteById(id);
    }
}
