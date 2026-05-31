package com.erp.projects.service;

import com.erp.projects.entity.TimeLog;
import com.erp.projects.repository.TimeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeLogService {

    private final TimeLogRepository timeLogRepository;

    @Transactional(readOnly = true)
    public List<TimeLog> all() {
        return timeLogRepository.findAllByOrderByDateDesc();
    }

    @Transactional(readOnly = true)
    public List<TimeLog> byProject(UUID projectId) {
        return timeLogRepository.findByProjectIdOrderByDateDesc(projectId);
    }

    @Transactional(readOnly = true)
    public List<TimeLog> byTask(UUID taskId) {
        return timeLogRepository.findByTaskIdOrderByDateDesc(taskId);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalHours(List<TimeLog> logs) {
        return logs.stream()
                .map(TimeLog::getHours)
                .filter(h -> h != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TimeLog save(TimeLog log) {
        return timeLogRepository.save(log);
    }

    public void delete(UUID id) {
        timeLogRepository.deleteById(id);
    }
}
