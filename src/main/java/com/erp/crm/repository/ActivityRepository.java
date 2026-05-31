package com.erp.crm.repository;

import com.erp.crm.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    @Query("SELECT a FROM Activity a WHERE :q IS NULL OR LOWER(a.subject) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Activity> search(@Param("q") String q, Pageable pageable);

    List<Activity> findByRelatedIdOrderByCreatedAtDesc(UUID relatedId);
    List<Activity> findTop8ByCompletedFalseOrderByDueDateAsc();
}
