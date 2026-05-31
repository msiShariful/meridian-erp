package com.erp.crm.enums;

import lombok.Getter;

@Getter
public enum LeadStatus {
    NEW("New", "info"),
    CONTACTED("Contacted", "brand"),
    QUALIFIED("Qualified", "warning"),
    PROPOSAL("Proposal", "warning"),
    WON("Won", "success"),
    LOST("Lost", "danger");

    private final String label;
    private final String color;

    LeadStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    /** Pipeline columns for the Kanban board (excludes terminal states from drag flow). */
    public static LeadStatus[] pipeline() {
        return new LeadStatus[]{NEW, CONTACTED, QUALIFIED, PROPOSAL, WON, LOST};
    }
}
