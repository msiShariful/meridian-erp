package com.erp.procurement.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.procurement.enums.GRNStatus;
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

import java.time.LocalDate;

@Entity
@Table(name = "proc_grns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GRN extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String grnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate receivedDate = LocalDate.now();

    @Column(length = 120)
    private String receivedBy;

    @Column(length = 2000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private GRNStatus status = GRNStatus.COMPLETE;
}
