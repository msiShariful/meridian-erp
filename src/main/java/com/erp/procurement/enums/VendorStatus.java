package com.erp.procurement.enums;

import lombok.Getter;

@Getter
public enum VendorStatus {
    ACTIVE("Active", "success"),
    INACTIVE("Inactive", "warning"),
    BLACKLISTED("Blacklisted", "danger");

    private final String label;
    private final String color;

    VendorStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
