package com.erp.projects.repository;

import com.erp.projects.entity.Task;
import com.erp.projects.enums.TaskPriority;
import com.erp.projects.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    List<Task> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    @Query("SELECT t FROM Task t WHERE (:status IS NULL OR t.status = :status) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "ORDER BY t.createdAt DESC")
    List<Task> filter(@Param("status") TaskStatus status, @Param("priority") TaskPriority priority);

    long countByStatus(TaskStatus status);
}
