package com.erp.support.enums;

import lombok.Getter;

@Getter
public enum TicketPriority {
    LOW("Low", "info", 48),
    MEDIUM("Medium", "brand", 24),
    HIGH("High", "warning", 8),
    CRITICAL("Critical", "danger", 4);

    private final String label;
    private final String color;
    /** Default SLA resolution hours for this priority. */
    private final int defaultResolutionHours;

    TicketPriority(String label, String color, int defaultResolutionHours) {
        this.label = label;
        this.color = color;
        this.defaultResolutionHours = defaultResolutionHours;
    }

    /** Default SLA first-response hours (a quarter of the resolution window, min 1). */
    public int getDefaultResponseHours() {
        return Math.max(1, defaultResolutionHours / 4);
    }
}
