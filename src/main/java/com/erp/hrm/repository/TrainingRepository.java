package com.erp.hrm.repository;

import com.erp.hrm.entity.Training;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TrainingRepository extends JpaRepository<Training, UUID> {
    @Query("SELECT t FROM Training t WHERE :q IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(t.trainer) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Training> search(@Param("q") String q, Pageable pageable);
}
