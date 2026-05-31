package com.erp.accounting.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "acc_invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private int quantity = 1;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal lineTotal = BigDecimal.ZERO;
}
