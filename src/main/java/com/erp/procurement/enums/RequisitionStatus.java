package com.erp.procurement.enums;

import lombok.Getter;

@Getter
public enum RequisitionStatus {
    DRAFT("Draft", "slate"),
    SUBMITTED("Submitted", "info"),
    APPROVED("Approved", "success"),
    REJECTED("Rejected", "danger");

    private final String label;
    private final String color;

    RequisitionStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
