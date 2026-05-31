package com.erp.accounting.enums;

import lombok.Getter;

@Getter
public enum InvoiceStatus {
    DRAFT("Draft", "default"),
    SENT("Sent", "info"),
    PARTIAL("Partial", "warning"),
    PAID("Paid", "success"),
    OVERDUE("Overdue", "danger");

    private final String label;
    private final String color;

    InvoiceStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
