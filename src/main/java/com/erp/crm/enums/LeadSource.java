package com.erp.crm.enums;

import lombok.Getter;

@Getter
public enum LeadSource {
    WEB("Web"), PHONE("Phone"), EMAIL("Email"), REFERRAL("Referral"), SOCIAL("Social Media");

    private final String label;
    LeadSource(String label) { this.label = label; }
}
