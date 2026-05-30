package com.erp.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Granular module permissions used for method-level and URL security.
 */
public enum PermissionType {
    DASHBOARD_VIEW,
    CRM_VIEW, CRM_EDIT,
    HRM_VIEW, HRM_EDIT,
    INVENTORY_VIEW, INVENTORY_EDIT,
    ECOMMERCE_VIEW, ECOMMERCE_EDIT,
    ACCOUNTING_VIEW, ACCOUNTING_EDIT,
    PROCUREMENT_VIEW, PROCUREMENT_EDIT,
    PROJECTS_VIEW, PROJECTS_EDIT,
    SUPPORT_VIEW, SUPPORT_EDIT,
    REPORTS_VIEW,
    SETTINGS_VIEW, SETTINGS_EDIT,
    USER_MANAGE, ROLE_MANAGE, AUDIT_VIEW;

    public static List<String> moduleEditPermissions() {
        return Arrays.stream(values())
                .filter(p -> p.name().endsWith("_EDIT") || p.name().endsWith("_MANAGE"))
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
