package com.erp.support.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.support.entity.TicketCategory;
import com.erp.support.repository.TicketCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketCategoryService {

    private final TicketCategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<TicketCategory> all() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TicketCategory get(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("TicketCategory", id));
    }

    @Transactional
    public TicketCategory save(TicketCategory category) {
        return categoryRepository.save(category);
    }
}
