package com.erp.hrm.repository;

import com.erp.hrm.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, UUID> {
}
