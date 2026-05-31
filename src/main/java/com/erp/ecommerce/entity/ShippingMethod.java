package com.erp.ecommerce.entity;

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
@Table(name = "ec_shipping_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingMethod extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal rate = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal freeAbove;
}
