package com.erp.accounting.enums;

import lombok.Getter;

@Getter
public enum ExpenseStatus {
    PENDING("Pending", "warning"),
    APPROVED("Approved", "info"),
    REJECTED("Rejected", "danger"),
    REIMBURSED("Reimbursed", "success");

    private final String label;
    private final String color;

    ExpenseStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
