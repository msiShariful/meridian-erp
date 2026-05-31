package com.erp.accounting.entity;

import com.erp.accounting.enums.InvoiceStatus;
import com.erp.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "acc_invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String invoiceNumber;

    @Column(nullable = false, length = 150)
    private String customerName;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate issueDate = LocalDate.now();

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 2000)
    private String notes;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "item_order")
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();
}
