package com.erp.inventory.enums;

import lombok.Getter;

@Getter
public enum MovementType {
    IN("Stock In", "success"),
    OUT("Stock Out", "danger"),
    ADJUSTMENT("Adjustment", "warning"),
    TRANSFER("Transfer", "info");

    private final String label;
    private final String color;

    MovementType(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
