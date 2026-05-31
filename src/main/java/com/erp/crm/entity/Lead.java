package com.erp.crm.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.crm.enums.LeadSource;
import com.erp.crm.enums.LeadStatus;
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

@Entity
@Table(name = "crm_leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 150)
    private String companyName;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LeadSource source = LeadSource.WEB;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Column(nullable = false)
    @Builder.Default
    private int score = 3;

    @Column(length = 120)
    private String assignedTo;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal estimatedValue = BigDecimal.ZERO;

    @Column(length = 2000)
    private String notes;

    @Column(length = 255)
    private String lostReason;

    public String getInitials() {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        return (p.length == 1 ? p[0].substring(0, 1) : "" + p[0].charAt(0) + p[p.length - 1].charAt(0)).toUpperCase();
    }
}
