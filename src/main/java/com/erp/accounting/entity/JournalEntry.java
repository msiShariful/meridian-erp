package com.erp.accounting.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "acc_journal_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry extends BaseEntity {

    @Column(nullable = false)
    @Builder.Default
    private LocalDate date = LocalDate.now();

    @Column(length = 50)
    private String reference;

    @Column(length = 255)
    private String description;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "line_order")
    @Builder.Default
    private List<JournalEntryLine> lines = new ArrayList<>();
}
