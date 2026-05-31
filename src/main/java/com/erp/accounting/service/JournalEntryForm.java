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
 * Form-backing object for journal entry creation, carrying debit/credit lines
 * bound via indexed params (lines[0].accountId, lines[0].debit, ...).
 */
@Getter
@Setter
@NoArgsConstructor
public class JournalEntryForm {

    private UUID id;
    private LocalDate date = LocalDate.now();
    private String reference;
    private String description;
    private List<Line> lines = new ArrayList<>();

    public boolean isNew() {
        return id == null;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Line {
        private UUID accountId;
        private BigDecimal debit = BigDecimal.ZERO;
        private BigDecimal credit = BigDecimal.ZERO;
    }
}
