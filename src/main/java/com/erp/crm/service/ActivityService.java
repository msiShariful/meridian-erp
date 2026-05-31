package com.erp.crm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.crm.entity.Activity;
import com.erp.crm.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Transactional(readOnly = true)
    public Page<Activity> search(String q, Pageable pageable) { return activityRepository.search(blank(q), pageable); }

    @Transactional(readOnly = true)
    public Activity get(UUID id) {
        return activityRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Activity", id));
    }

    @Transactional(readOnly = true)
    public List<Activity> forRecord(UUID relatedId) { return activityRepository.findByRelatedIdOrderByCreatedAtDesc(relatedId); }

    @Transactional(readOnly = true)
    public List<Activity> upcoming() { return activityRepository.findTop8ByCompletedFalseOrderByDueDateAsc(); }

    @Transactional
    public Activity save(Activity a) { return activityRepository.save(a); }

    @Transactional
    public void toggleComplete(UUID id) {
        Activity a = get(id);
        a.setCompleted(!a.isCompleted());
        activityRepository.save(a);
    }

    @Transactional
    public void delete(UUID id) { activityRepository.deleteById(id); }

    private String blank(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
}
