package com.erp.ecommerce.enums;

import lombok.Getter;

@Getter
public enum ReviewStatus {
    PENDING("Pending", "warning"),
    APPROVED("Approved", "success"),
    REJECTED("Rejected", "danger");

    private final String label;
    private final String color;

    ReviewStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
