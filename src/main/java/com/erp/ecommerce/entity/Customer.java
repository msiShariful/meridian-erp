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
@Table(name = "ec_customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 500)
    private String address;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private int ordersCount = 0;

    public String getInitials() {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        return (p.length == 1 ? p[0].substring(0, 1) : "" + p[0].charAt(0) + p[p.length - 1].charAt(0)).toUpperCase();
    }
}
