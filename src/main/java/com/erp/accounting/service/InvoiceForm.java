package com.erp.accounting.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Form-backing object for invoice create/edit, carrying a variable list of
 * line items bound via indexed params (items[0].description, ...).
 */
@Getter
@Setter
@NoArgsConstructor
public class InvoiceForm {

    private UUID id;
    private String invoiceNumber;
    private String customerName;
    private LocalDate issueDate = LocalDate.now();
    private LocalDate dueDate = LocalDate.now().plusDays(30);
    private BigDecimal taxRate = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
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
