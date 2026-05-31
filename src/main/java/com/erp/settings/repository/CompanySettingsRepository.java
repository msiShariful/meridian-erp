package com.erp.settings.repository;

import com.erp.settings.entity.CompanySettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanySettingsRepository extends JpaRepository<CompanySettings, UUID> {
}
