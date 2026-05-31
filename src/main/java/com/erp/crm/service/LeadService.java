package com.erp.crm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.crm.entity.Contact;
import com.erp.crm.entity.Deal;
import com.erp.crm.entity.Lead;
import com.erp.crm.enums.DealStage;
import com.erp.crm.enums.LeadStatus;
import com.erp.crm.repository.ContactRepository;
import com.erp.crm.repository.DealRepository;
import com.erp.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final ContactRepository contactRepository;
    private final DealRepository dealRepository;

    @Transactional(readOnly = true)
    public Page<Lead> search(String q, LeadStatus status, Pageable pageable) {
        return leadRepository.search(blank(q), status, pageable);
    }

    @Transactional(readOnly = true)
    public Lead get(UUID id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Lead", id));
    }

    @Transactional(readOnly = true)
    public List<Lead> byStatus(LeadStatus status) {
        return leadRepository.findByStatus(status);
    }

    @Transactional
    public Lead save(Lead lead) {
        return leadRepository.save(lead);
    }

    @Transactional
    public void updateStatus(UUID id, LeadStatus status) {
        Lead lead = get(id);
        lead.setStatus(status);
        leadRepository.save(lead);
    }

    @Transactional
    public void delete(UUID id) {
        leadRepository.deleteById(id);
    }

    /** Converts a won lead into a Contact and an open Deal. */
    @Transactional
    public Deal convert(UUID leadId) {
        Lead lead = get(leadId);
        String[] parts = lead.getName().trim().split("\\s+", 2);
        Contact contact = Contact.builder()
                .firstName(parts[0])
                .lastName(parts.length > 1 ? parts[1] : "")
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .build();
        contactRepository.save(contact);

        Deal deal = Deal.builder()
                .title(lead.getName() + " — " + (lead.getCompanyName() != null ? lead.getCompanyName() : "Opportunity"))
                .value(lead.getEstimatedValue() != null ? lead.getEstimatedValue() : BigDecimal.ZERO)
                .stage(DealStage.QUALIFICATION)
                .probability(DealStage.QUALIFICATION.getDefaultProbability())
                .owner(lead.getAssignedTo())
                .contact(contact)
                .build();
        dealRepository.save(deal);

        lead.setStatus(LeadStatus.WON);
        leadRepository.save(lead);
        return deal;
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
