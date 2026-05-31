package com.erp.support.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.support.entity.FAQ;
import com.erp.support.repository.FAQRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FAQService {

    private final FAQRepository faqRepository;

    @Transactional(readOnly = true)
    public List<FAQ> all() {
        return faqRepository.findAllByOrderByCategoryAscQuestionAsc();
    }

    /** FAQs grouped by category, preserving alphabetical ordering. */
    @Transactional(readOnly = true)
    public Map<String, List<FAQ>> grouped() {
        Map<String, List<FAQ>> groups = new LinkedHashMap<>();
        for (FAQ faq : all()) {
            String key = (faq.getCategory() == null || faq.getCategory().isBlank()) ? "General" : faq.getCategory();
            groups.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(faq);
        }
        return groups;
    }

    @Transactional(readOnly = true)
    public FAQ get(UUID id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("FAQ", id));
    }

    @Transactional
    public FAQ save(FAQ faq) {
        return faqRepository.save(faq);
    }

    @Transactional
    public void delete(UUID id) {
        faqRepository.deleteById(id);
    }
}
