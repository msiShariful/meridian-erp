package com.erp.procurement.enums;

import lombok.Getter;

@Getter
public enum BillStatus {
    PENDING("Pending", "warning"),
    PAID("Paid", "success");

    private final String label;
    private final String color;

    BillStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
