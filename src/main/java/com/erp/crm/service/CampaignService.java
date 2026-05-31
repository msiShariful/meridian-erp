package com.erp.crm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.crm.entity.Campaign;
import com.erp.crm.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;

    @Transactional(readOnly = true)
    public Page<Campaign> search(String q, Pageable pageable) { return campaignRepository.search(blank(q), pageable); }

    @Transactional(readOnly = true)
    public Campaign get(UUID id) {
        return campaignRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Campaign", id));
    }

    @Transactional
    public Campaign save(Campaign c) { return campaignRepository.save(c); }

    @Transactional
    public void delete(UUID id) { campaignRepository.deleteById(id); }

    private String blank(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
}
