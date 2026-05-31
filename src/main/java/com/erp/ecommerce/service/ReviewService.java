package com.erp.ecommerce.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.ecommerce.entity.Review;
import com.erp.ecommerce.enums.ReviewStatus;
import com.erp.ecommerce.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Page<Review> search(String q, ReviewStatus status, Pageable pageable) {
        return reviewRepository.search(blank(q), status, pageable);
    }

    @Transactional(readOnly = true)
    public Review get(UUID id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Review", id));
    }

    @Transactional(readOnly = true)
    public List<Review> approvedFor(String productName) {
        return reviewRepository.findByProductNameAndStatusOrderByCreatedAtDesc(productName, ReviewStatus.APPROVED);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public void updateStatus(UUID id, ReviewStatus status) {
        Review review = get(id);
        review.setStatus(status);
        reviewRepository.save(review);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
