package com.erp.inventory.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.inventory.entity.Product;
import com.erp.inventory.repository.ProductRepository;
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
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Product> search(String q, Pageable pageable) {
        return productRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> all() {
        return productRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public Product get(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));
    }

    @Transactional(readOnly = true)
    public List<Product> lowStock() {
        return productRepository.findLowStock();
    }

    public Product save(Product product) {
        if (product.getSku() == null || product.getSku().isBlank()) {
            product.setSku(nextSku());
        }
        return productRepository.save(product);
    }

    /** Generates the next sequential SKU like "SKU-0001". */
    @Transactional(readOnly = true)
    public String nextSku() {
        long next = productRepository.count() + 1;
        return String.format("SKU-%04d", next);
    }

    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
