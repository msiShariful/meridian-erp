package com.erp.accounting.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH("Cash"),
    BANK("Bank Transfer"),
    CARD("Card"),
    MOBILE("Mobile Banking");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }
}
