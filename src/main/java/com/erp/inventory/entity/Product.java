package com.erp.inventory.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "inv_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(nullable = false, length = 40, unique = true)
    private String sku;

    @Column(nullable = false, length = 180)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private int stockQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private int reorderLevel = 0;

    @Column(length = 30)
    private String unit;

    @Column(length = 60)
    private String barcode;

    @Column(length = 255)
    private String image;

    @Column(length = 1000)
    private String description;

    @Transient
    public String getStockStatus() {
        if (stockQuantity == 0) return "Out";
        if (stockQuantity <= reorderLevel) return "Low";
        return "In Stock";
    }

    @Transient
    public String getStockStatusColor() {
        if (stockQuantity == 0) return "danger";
        if (stockQuantity <= reorderLevel) return "warning";
        return "success";
    }

    public String getInitials() {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        return (p.length == 1 ? p[0].substring(0, 1) : "" + p[0].charAt(0) + p[1].charAt(0)).toUpperCase();
    }
}
