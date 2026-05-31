package com.erp.ecommerce.repository;

import com.erp.ecommerce.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    @Query("SELECT c FROM Coupon c WHERE :q IS NULL OR LOWER(c.code) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Coupon> search(@Param("q") String q, Pageable pageable);

    Optional<Coupon> findByCodeIgnoreCase(String code);
}
