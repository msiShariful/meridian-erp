package com.erp.crm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.crm.entity.Contact;
import com.erp.crm.repository.ContactRepository;
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
public class ContactService {

    private final ContactRepository contactRepository;

    @Transactional(readOnly = true)
    public Page<Contact> search(String q, Pageable pageable) {
        return contactRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<Contact> all() { return contactRepository.findAll(Sort.by("firstName")); }

    @Transactional(readOnly = true)
    public Contact get(UUID id) {
        return contactRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Contact", id));
    }

    @Transactional(readOnly = true)
    public List<Contact> byCompany(UUID companyId) { return contactRepository.findByCompanyId(companyId); }

    @Transactional
    public Contact save(Contact c) { return contactRepository.save(c); }

    @Transactional
    public void delete(UUID id) { contactRepository.deleteById(id); }

    private String blank(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
}
