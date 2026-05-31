package com.erp.hrm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.hrm.entity.Training;
import com.erp.hrm.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;

    @Transactional(readOnly = true)
    public Page<Training> search(String q, Pageable pageable) {
        return trainingRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public Training get(UUID id) {
        return trainingRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Training", id));
    }

    @Transactional
    public Training save(Training training) {
        return trainingRepository.save(training);
    }

    @Transactional
    public void delete(UUID id) {
        trainingRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
