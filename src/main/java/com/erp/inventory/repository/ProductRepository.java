package com.erp.inventory.repository;

import com.erp.inventory.entity.Product;
import com.erp.inventory.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE :q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(p.barcode) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Product> search(@Param("q") String q, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.reorderLevel ORDER BY p.stockQuantity ASC")
    List<Product> findLowStock();

    long countByCategory(ProductCategory category);
}
