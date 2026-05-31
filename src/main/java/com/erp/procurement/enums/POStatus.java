package com.erp.procurement.enums;

import lombok.Getter;

@Getter
public enum POStatus {
    DRAFT("Draft", "slate"),
    SENT("Sent", "info"),
    PARTIAL("Partially Received", "warning"),
    RECEIVED("Received", "success"),
    CLOSED("Closed", "brand");

    private final String label;
    private final String color;

    POStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
