package com.erp.crm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.crm.entity.Company;
import com.erp.crm.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public Page<Company> search(String q, Pageable pageable) {
        return companyRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<Company> all() {
        return companyRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public Company get(UUID id) {
        return companyRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Company", id));
    }

    @Transactional
    public Company save(Company c) { return companyRepository.save(c); }

    @Transactional
    public void delete(UUID id) { companyRepository.deleteById(id); }

    private String blank(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
}
