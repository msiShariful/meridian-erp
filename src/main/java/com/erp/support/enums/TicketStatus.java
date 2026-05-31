package com.erp.support.enums;

import lombok.Getter;

@Getter
public enum TicketStatus {
    OPEN("Open", "info"),
    IN_PROGRESS("In Progress", "brand"),
    WAITING("Waiting", "warning"),
    RESOLVED("Resolved", "success"),
    CLOSED("Closed", "default");

    private final String label;
    private final String color;

    TicketStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
