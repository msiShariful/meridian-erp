package com.erp.accounting.repository;

import com.erp.accounting.entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaxRateRepository extends JpaRepository<TaxRate, UUID> {
}
