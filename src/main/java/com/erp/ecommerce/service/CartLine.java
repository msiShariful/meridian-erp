package com.erp.ecommerce.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** A priced, display-ready line of the storefront cart. */
@Getter
@RequiredArgsConstructor
public class CartLine {
    private final UUID productId;
    private final String productName;
    private final String image;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;
}
