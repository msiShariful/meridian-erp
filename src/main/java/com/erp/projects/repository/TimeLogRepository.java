package com.erp.projects.repository;

import com.erp.projects.entity.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TimeLogRepository extends JpaRepository<TimeLog, UUID> {

    List<TimeLog> findAllByOrderByDateDesc();

    List<TimeLog> findByProjectIdOrderByDateDesc(UUID projectId);

    List<TimeLog> findByTaskIdOrderByDateDesc(UUID taskId);
}
