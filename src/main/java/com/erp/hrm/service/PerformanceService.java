package com.erp.hrm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.hrm.entity.PerformanceReview;
import com.erp.hrm.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceReviewRepository performanceReviewRepository;

    @Transactional(readOnly = true)
    public Page<PerformanceReview> list(Pageable pageable) {
        return performanceReviewRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public PerformanceReview get(UUID id) {
        return performanceReviewRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("PerformanceReview", id));
    }

    @Transactional
    public PerformanceReview save(PerformanceReview review) {
        return performanceReviewRepository.save(review);
    }

    @Transactional
    public void delete(UUID id) {
        performanceReviewRepository.deleteById(id);
    }
}
