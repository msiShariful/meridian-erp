package com.erp.accounting.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    ASSET("Asset", "info"),
    LIABILITY("Liability", "warning"),
    EQUITY("Equity", "brand"),
    REVENUE("Revenue", "success"),
    EXPENSE("Expense", "danger");

    private final String label;
    private final String color;

    AccountType(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
