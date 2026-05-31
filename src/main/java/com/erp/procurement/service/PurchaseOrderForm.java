package com.erp.procurement.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Form-backing object for purchase order create/edit, carrying a variable list
 * of line items bound via indexed params (items[0].description, ...).
 */
@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrderForm {

    private UUID id;
    private String poNumber;
    private UUID vendorId;
    private LocalDate orderDate = LocalDate.now();
    private LocalDate expectedDate = LocalDate.now().plusDays(14);
    private String notes;
    private List<Item> items = new ArrayList<>();

    public boolean isNew() {
        return id == null;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {
        private String description;
        private int quantity;
        private BigDecimal unitPrice = BigDecimal.ZERO;
    }
}
