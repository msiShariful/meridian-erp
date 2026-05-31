package com.erp.hrm.enums;

import lombok.Getter;

@Getter
public enum EmployeeStatus {
    ACTIVE("Active", "success"),
    ON_LEAVE("On Leave", "warning"),
    RESIGNED("Resigned", "info"),
    TERMINATED("Terminated", "danger");

    private final String label;
    private final String color;

    EmployeeStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
