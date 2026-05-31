package com.erp.ecommerce.enums;

import lombok.Getter;

@Getter
public enum CouponType {
    PERCENT("Percentage", "brand"),
    FIXED("Fixed Amount", "info");

    private final String label;
    private final String color;

    CouponType(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
