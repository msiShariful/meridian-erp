package com.erp.accounting.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "acc_tax_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxRate extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal rate = BigDecimal.ZERO;
}
