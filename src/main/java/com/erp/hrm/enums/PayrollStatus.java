package com.erp.hrm.enums;

import lombok.Getter;

@Getter
public enum PayrollStatus {
    DRAFT("Draft", "info"),
    PROCESSED("Processed", "warning"),
    PAID("Paid", "success");

    private final String label;
    private final String color;

    PayrollStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
