package com.erp.accounting.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "acc_budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String categoryName;

    @Column(nullable = false, length = 50)
    private String period;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal budgetedAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal actualAmount = BigDecimal.ZERO;

    /** Remaining budget: budgeted minus actual. Positive => under budget. */
    @Transient
    public BigDecimal variance() {
        BigDecimal b = budgetedAmount != null ? budgetedAmount : BigDecimal.ZERO;
        BigDecimal a = actualAmount != null ? actualAmount : BigDecimal.ZERO;
        return b.subtract(a);
    }

    /** Actual spend as a percentage of budgeted amount. */
    @Transient
    public BigDecimal variancePercent() {
        BigDecimal b = budgetedAmount != null ? budgetedAmount : BigDecimal.ZERO;
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal a = actualAmount != null ? actualAmount : BigDecimal.ZERO;
        return a.multiply(BigDecimal.valueOf(100)).divide(b, 1, RoundingMode.HALF_UP);
    }
}
