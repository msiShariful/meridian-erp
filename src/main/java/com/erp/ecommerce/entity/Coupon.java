package com.erp.ecommerce.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.ecommerce.enums.CouponType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ec_coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    @Column(nullable = false, length = 40, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CouponType type = CouponType.PERCENT;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal value = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private int usageLimit = 0;

    @Column(nullable = false)
    @Builder.Default
    private int usedCount = 0;

    private LocalDate expiryDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @jakarta.persistence.Transient
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    @jakarta.persistence.Transient
    public boolean isUsable() {
        return active && !isExpired() && (usageLimit == 0 || usedCount < usageLimit);
    }
}
