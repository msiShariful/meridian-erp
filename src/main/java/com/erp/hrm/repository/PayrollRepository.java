package com.erp.hrm.repository;

import com.erp.hrm.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PayrollRepository extends JpaRepository<Payroll, UUID> {
}
