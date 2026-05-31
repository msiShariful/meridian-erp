package com.erp.inventory.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.inventory.entity.Supplier;
import com.erp.inventory.repository.SupplierRepository;
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
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public Page<Supplier> search(String q, Pageable pageable) {
        return supplierRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<Supplier> all() {
        return supplierRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public Supplier get(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Supplier", id));
    }

    public Supplier save(Supplier s) {
        return supplierRepository.save(s);
    }

    public void delete(UUID id) {
        supplierRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
