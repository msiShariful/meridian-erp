package com.erp.projects.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    PLANNING("Planning", "info"),
    ACTIVE("Active", "success"),
    ON_HOLD("On Hold", "warning"),
    COMPLETED("Completed", "brand");

    private final String label;
    private final String color;

    ProjectStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
