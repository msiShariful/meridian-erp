package com.erp.crm.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.crm.enums.CampaignStatus;
import com.erp.crm.enums.CampaignType;
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
@Table(name = "crm_campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CampaignType type = CampaignType.EMAIL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CampaignStatus status = CampaignStatus.PLANNED;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal budget = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal revenue = BigDecimal.ZERO;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    @Builder.Default
    private int leadsGenerated = 0;

    @Column(length = 1000)
    private String description;

    /** Return on investment as a percentage. */
    public BigDecimal getRoi() {
        if (budget == null || budget.signum() == 0) return BigDecimal.ZERO;
        return revenue.subtract(budget)
                .multiply(BigDecimal.valueOf(100))
                .divide(budget, 1, java.math.RoundingMode.HALF_UP);
    }
}
