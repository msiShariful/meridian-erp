package com.erp.projects.enums;

import lombok.Getter;

@Getter
public enum TaskPriority {
    LOW("Low", "info"),
    MEDIUM("Medium", "brand"),
    HIGH("High", "warning"),
    CRITICAL("Critical", "danger");

    private final String label;
    private final String color;

    TaskPriority(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
