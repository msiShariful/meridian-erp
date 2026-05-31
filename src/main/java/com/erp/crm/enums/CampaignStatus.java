package com.erp.crm.enums;

import lombok.Getter;

@Getter
public enum CampaignStatus {
    PLANNED("Planned", "info"), ACTIVE("Active", "success"),
    COMPLETED("Completed", "brand"), CANCELLED("Cancelled", "danger");

    private final String label;
    private final String color;
    CampaignStatus(String label, String color) { this.label = label; this.color = color; }
}
