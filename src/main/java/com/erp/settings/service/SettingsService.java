package com.erp.settings.service;

import com.erp.settings.entity.CompanySettings;
import com.erp.settings.entity.EmailSettings;
import com.erp.settings.repository.CompanySettingsRepository;
import com.erp.settings.repository.EmailSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages the two singleton settings rows (company + email), creating defaults on first access.
 */
@Service
@RequiredArgsConstructor
public class SettingsService {

    private final CompanySettingsRepository companyRepository;
    private final EmailSettingsRepository emailRepository;

    @Transactional
    public CompanySettings company() {
        return companyRepository.findAll().stream().findFirst()
                .orElseGet(() -> companyRepository.save(CompanySettings.builder().build()));
    }

    @Transactional
    public CompanySettings saveCompany(CompanySettings incoming) {
        CompanySettings current = company();
        current.setCompanyName(incoming.getCompanyName());
        current.setAddress(incoming.getAddress());
        current.setPhone(incoming.getPhone());
        current.setEmail(incoming.getEmail());
        current.setWebsite(incoming.getWebsite());
        current.setCurrency(incoming.getCurrency());
        current.setTimezone(incoming.getTimezone());
        current.setFiscalYearStart(incoming.getFiscalYearStart());
        if (incoming.getLogo() != null && !incoming.getLogo().isBlank()) {
            current.setLogo(incoming.getLogo());
        }
        return companyRepository.save(current);
    }

    @Transactional
    public EmailSettings email() {
        return emailRepository.findAll().stream().findFirst()
                .orElseGet(() -> emailRepository.save(EmailSettings.builder().build()));
    }

    @Transactional
    public EmailSettings saveEmail(EmailSettings incoming) {
        EmailSettings current = email();
        current.setSmtpHost(incoming.getSmtpHost());
        current.setSmtpPort(incoming.getSmtpPort());
        current.setUsername(incoming.getUsername());
        current.setPassword(incoming.getPassword());
        current.setTlsEnabled(incoming.isTlsEnabled());
        current.setFromAddress(incoming.getFromAddress());
        return emailRepository.save(current);
    }
}
