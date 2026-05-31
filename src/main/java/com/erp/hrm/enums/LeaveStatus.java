package com.erp.hrm.enums;

import lombok.Getter;

@Getter
public enum LeaveStatus {
    PENDING("Pending", "warning"),
    APPROVED("Approved", "success"),
    REJECTED("Rejected", "danger");

    private final String label;
    private final String color;

    LeaveStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
