package com.erp.procurement.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.procurement.enums.RequisitionStatus;
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
@Table(name = "proc_requisitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requisition extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String reqNumber;

    @Column(length = 120)
    private String department;

    @Column(length = 120)
    private String requestedBy;

    @Column(length = 2000)
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RequisitionStatus status = RequisitionStatus.DRAFT;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal estimatedCost = BigDecimal.ZERO;

    @Column(length = 2000)
    private String items;
}
