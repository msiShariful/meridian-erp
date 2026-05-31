package com.erp.procurement.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.procurement.entity.Vendor;
import com.erp.procurement.repository.VendorRepository;
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
public class VendorService {

    private final VendorRepository vendorRepository;

    @Transactional(readOnly = true)
    public Page<Vendor> search(String q, Pageable pageable) {
        return vendorRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<Vendor> all() {
        return vendorRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Vendor get(UUID id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Vendor", id));
    }

    public Vendor save(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    public void delete(UUID id) {
        vendorRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
