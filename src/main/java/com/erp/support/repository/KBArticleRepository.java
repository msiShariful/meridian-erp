package com.erp.support.repository;

import com.erp.support.entity.KBArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface KBArticleRepository extends JpaRepository<KBArticle, UUID> {
    @Query("SELECT a FROM KBArticle a WHERE :q IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(a.category) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(a.tags) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<KBArticle> search(@Param("q") String q, Pageable pageable);
}
