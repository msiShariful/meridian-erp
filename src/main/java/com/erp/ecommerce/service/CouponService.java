package com.erp.ecommerce.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.ecommerce.entity.Coupon;
import com.erp.ecommerce.enums.CouponType;
import com.erp.ecommerce.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public Page<Coupon> search(String q, Pageable pageable) {
        return couponRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public Coupon get(UUID id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Coupon", id));
    }

    @Transactional(readOnly = true)
    public Optional<Coupon> findUsable(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        return couponRepository.findByCodeIgnoreCase(code.trim())
                .filter(Coupon::isUsable);
    }

    /** Computes the discount this coupon yields for a given subtotal (0 if not applicable). */
    public BigDecimal discountFor(Coupon coupon, BigDecimal subtotal) {
        if (coupon == null || subtotal == null) return BigDecimal.ZERO;
        if (subtotal.compareTo(coupon.getMinOrderValue()) < 0) return BigDecimal.ZERO;
        BigDecimal discount = coupon.getType() == CouponType.PERCENT
                ? subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100))
                : coupon.getValue();
        if (discount.compareTo(subtotal) > 0) discount = subtotal;
        return discount.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void delete(UUID id) {
        couponRepository.deleteById(id);
    }

    public void incrementUsage(UUID id) {
        Coupon coupon = get(id);
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
