package com.erp.accounting.service;

import com.erp.accounting.entity.Account;
import com.erp.accounting.repository.AccountRepository;
import com.erp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Page<Account> search(String q, Pageable pageable) {
        return accountRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<Account> all() {
        return accountRepository.findAllByOrderByTypeAscCodeAsc();
    }

    @Transactional(readOnly = true)
    public Account get(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Account", id));
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public void delete(UUID id) {
        accountRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
