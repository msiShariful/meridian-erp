package com.erp.inventory.repository;

import com.erp.inventory.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

    @Query("SELECT c FROM ProductCategory c WHERE :q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<ProductCategory> search(@Param("q") String q, Pageable pageable);
}
