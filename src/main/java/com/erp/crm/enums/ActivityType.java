package com.erp.crm.enums;

import lombok.Getter;

@Getter
public enum ActivityType {
    CALL("Call"), EMAIL("Email"), MEETING("Meeting"), TASK("Task"), NOTE("Note");

    private final String label;
    ActivityType(String label) { this.label = label; }
}
