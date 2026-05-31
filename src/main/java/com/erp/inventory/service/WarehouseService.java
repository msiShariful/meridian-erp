package com.erp.inventory.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.inventory.entity.Warehouse;
import com.erp.inventory.repository.WarehouseRepository;
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
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    public Page<Warehouse> search(String q, Pageable pageable) {
        return warehouseRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<Warehouse> all() {
        return warehouseRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public Warehouse get(UUID id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Warehouse", id));
    }

    public Warehouse save(Warehouse w) {
        return warehouseRepository.save(w);
    }

    public void delete(UUID id) {
        warehouseRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
