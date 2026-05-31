package com.erp.inventory.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.inventory.entity.ProductCategory;
import com.erp.inventory.repository.ProductCategoryRepository;
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
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductCategory> search(String q, Pageable pageable) {
        return categoryRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<ProductCategory> all() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public ProductCategory get(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", id));
    }

    @Transactional(readOnly = true)
    public long productCount(ProductCategory category) {
        return productRepository.countByCategory(category);
    }

    public ProductCategory save(ProductCategory c) {
        return categoryRepository.save(c);
    }

    public void delete(UUID id) {
        categoryRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
