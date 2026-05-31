package com.erp.crm.enums;

import lombok.Getter;

@Getter
public enum DealStage {
    QUALIFICATION("Qualification", "info", 20),
    NEEDS_ANALYSIS("Needs Analysis", "brand", 40),
    PROPOSAL("Proposal", "warning", 60),
    NEGOTIATION("Negotiation", "warning", 80),
    WON("Won", "success", 100),
    LOST("Lost", "danger", 0);

    private final String label;
    private final String color;
    private final int defaultProbability;

    DealStage(String label, String color, int defaultProbability) {
        this.label = label;
        this.color = color;
        this.defaultProbability = defaultProbability;
    }

    public static DealStage[] pipeline() {
        return new DealStage[]{QUALIFICATION, NEEDS_ANALYSIS, PROPOSAL, NEGOTIATION, WON, LOST};
    }
}
