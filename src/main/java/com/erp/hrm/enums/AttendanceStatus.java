package com.erp.hrm.enums;

import lombok.Getter;

@Getter
public enum AttendanceStatus {
    PRESENT("Present", "success"),
    ABSENT("Absent", "danger"),
    LATE("Late", "warning"),
    HALF_DAY("Half Day", "info");

    private final String label;
    private final String color;

    AttendanceStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
