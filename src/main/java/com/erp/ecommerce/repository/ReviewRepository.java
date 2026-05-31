package com.erp.ecommerce.repository;

import com.erp.ecommerce.entity.Review;
import com.erp.ecommerce.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("SELECT r FROM Review r WHERE (:q IS NULL OR LOWER(r.productName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(r.customerName) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:status IS NULL OR r.status = :status)")
    Page<Review> search(@Param("q") String q, @Param("status") ReviewStatus status, Pageable pageable);

    List<Review> findByProductNameAndStatusOrderByCreatedAtDesc(String productName, ReviewStatus status);
}
