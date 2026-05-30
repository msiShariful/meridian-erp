package com.erp.common.enums;

/**
 * Canonical system roles. Spring Security authorities are prefixed with {@code ROLE_}.
 */
public enum RoleType {
    SUPER_ADMIN("Super Administrator"),
    ADMIN("Administrator"),
    HR_MANAGER("HR Manager"),
    SALES_MANAGER("Sales Manager"),
    ACCOUNTANT("Accountant"),
    INVENTORY_MANAGER("Inventory Manager"),
    SUPPORT_AGENT("Support Agent"),
    EMPLOYEE("Employee"),
    CUSTOMER("Customer");

    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String authority() {
        return "ROLE_" + name();
    }
}
