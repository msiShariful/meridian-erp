package com.erp.crm.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.crm.enums.DealStage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "crm_deals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal value = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    @Builder.Default
    private DealStage stage = DealStage.QUALIFICATION;

    @Column(nullable = false)
    @Builder.Default
    private int probability = 20;

    private LocalDate expectedCloseDate;

    @Column(length = 120)
    private String owner;

    @Column(length = 255)
    private String wonLostReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
