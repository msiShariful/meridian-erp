package com.erp.settings.repository;

import com.erp.settings.entity.EmailSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailSettingsRepository extends JpaRepository<EmailSettings, UUID> {
}
