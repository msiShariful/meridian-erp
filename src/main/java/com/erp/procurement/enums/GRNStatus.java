package com.erp.procurement.enums;

import lombok.Getter;

@Getter
public enum GRNStatus {
    PARTIAL("Partial", "warning"),
    COMPLETE("Complete", "success");

    private final String label;
    private final String color;

    GRNStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
