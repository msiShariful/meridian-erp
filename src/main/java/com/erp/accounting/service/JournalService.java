package com.erp.accounting.service;

import com.erp.accounting.entity.Account;
import com.erp.accounting.entity.JournalEntry;
import com.erp.accounting.entity.JournalEntryLine;
import com.erp.accounting.repository.AccountRepository;
import com.erp.accounting.repository.JournalEntryRepository;
import com.erp.common.exception.BusinessException;
import com.erp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Page<JournalEntry> search(String q, Pageable pageable) {
        return journalEntryRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public JournalEntry get(UUID id) {
        return journalEntryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Journal Entry", id));
    }

    public JournalEntry save(JournalEntryForm form) {
        JournalEntry entry = form.isNew() ? new JournalEntry() : get(form.getId());
        entry.setDate(form.getDate());
        entry.setReference(form.getReference());
        entry.setDescription(form.getDescription());
        entry.getLines().clear();

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        if (form.getLines() != null) {
            for (JournalEntryForm.Line fl : form.getLines()) {
                if (fl == null || fl.getAccountId() == null) {
                    continue;
                }
                BigDecimal debit = fl.getDebit() != null ? fl.getDebit() : BigDecimal.ZERO;
                BigDecimal credit = fl.getCredit() != null ? fl.getCredit() : BigDecimal.ZERO;
                if (debit.compareTo(BigDecimal.ZERO) == 0 && credit.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                Account account = accountRepository.findById(fl.getAccountId())
                        .orElseThrow(() -> ResourceNotFoundException.of("Account", fl.getAccountId()));
                JournalEntryLine line = JournalEntryLine.builder()
                        .entry(entry)
                        .account(account)
                        .debit(debit)
                        .credit(credit)
                        .build();
                entry.getLines().add(line);
                totalDebit = totalDebit.add(debit);
                totalCredit = totalCredit.add(credit);
            }
        }

        if (entry.getLines().size() < 2) {
            throw new BusinessException("A journal entry needs at least two lines.");
        }
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new BusinessException("Journal entry is not balanced: total debit (" + totalDebit
                    + ") must equal total credit (" + totalCredit + ").");
        }

        entry.setTotalAmount(totalDebit);
        return journalEntryRepository.save(entry);
    }

    public void delete(UUID id) {
        journalEntryRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
