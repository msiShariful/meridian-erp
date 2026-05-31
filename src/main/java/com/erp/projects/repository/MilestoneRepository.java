package com.erp.projects.repository;

import com.erp.projects.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MilestoneRepository extends JpaRepository<Milestone, UUID> {
    List<Milestone> findByProjectIdOrderByDueDateAsc(UUID projectId);
}
