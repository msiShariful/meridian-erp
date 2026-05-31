package com.erp.projects.repository;

import com.erp.projects.entity.Project;
import com.erp.projects.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("SELECT p FROM Project p WHERE (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(p.client) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:status IS NULL OR p.status = :status)")
    Page<Project> search(@Param("q") String q, @Param("status") ProjectStatus status, Pageable pageable);

    List<Project> findAllByOrderByCreatedAtDesc();
}
