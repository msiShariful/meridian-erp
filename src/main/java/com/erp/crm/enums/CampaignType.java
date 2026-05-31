package com.erp.crm.enums;

import lombok.Getter;

@Getter
public enum CampaignType {
    EMAIL("Email"), SMS("SMS"), EVENT("Event"), SOCIAL("Social"), WEBINAR("Webinar");

    private final String label;
    CampaignType(String label) { this.label = label; }
}
