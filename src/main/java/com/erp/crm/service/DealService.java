package com.erp.crm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.crm.entity.Deal;
import com.erp.crm.enums.DealStage;
import com.erp.crm.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;

    @Transactional(readOnly = true)
    public Page<Deal> search(String q, Pageable pageable) { return dealRepository.search(blank(q), pageable); }

    @Transactional(readOnly = true)
    public Deal get(UUID id) {
        return dealRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Deal", id));
    }

    @Transactional(readOnly = true)
    public List<Deal> byStage(DealStage stage) { return dealRepository.findByStage(stage); }

    @Transactional
    public Deal save(Deal d) {
        if (d.getStage() != null && d.getProbability() == 0 && d.getStage() != DealStage.LOST) {
            d.setProbability(d.getStage().getDefaultProbability());
        }
        return dealRepository.save(d);
    }

    @Transactional
    public void updateStage(UUID id, DealStage stage) {
        Deal deal = get(id);
        deal.setStage(stage);
        deal.setProbability(stage.getDefaultProbability());
        dealRepository.save(deal);
    }

    @Transactional
    public void delete(UUID id) { dealRepository.deleteById(id); }

    private String blank(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
}
