package com.erp.ecommerce.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pending", "warning"),
    PROCESSING("Processing", "brand"),
    SHIPPED("Shipped", "info"),
    DELIVERED("Delivered", "success"),
    CANCELLED("Cancelled", "danger"),
    REFUNDED("Refunded", "default");

    private final String label;
    private final String color;

    OrderStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
