package com.erp.hrm.service;

import com.erp.hrm.entity.Attendance;
import com.erp.hrm.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Transactional(readOnly = true)
    public Page<Attendance> byDate(LocalDate date, Pageable pageable) {
        return attendanceRepository.findByDate(date, pageable);
    }

    @Transactional
    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }
}
