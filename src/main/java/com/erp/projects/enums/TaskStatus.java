package com.erp.projects.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    TODO("To Do", "info"),
    IN_PROGRESS("In Progress", "brand"),
    REVIEW("Review", "warning"),
    DONE("Done", "success");

    private final String label;
    private final String color;

    TaskStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    /** Ordered columns for the Kanban task board. */
    public static TaskStatus[] board() {
        return new TaskStatus[]{TODO, IN_PROGRESS, REVIEW, DONE};
    }
}
