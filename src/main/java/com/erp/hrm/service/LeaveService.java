package com.erp.hrm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.hrm.entity.Leave;
import com.erp.hrm.entity.LeaveType;
import com.erp.hrm.enums.LeaveStatus;
import com.erp.hrm.repository.LeaveRepository;
import com.erp.hrm.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Transactional(readOnly = true)
    public Page<Leave> list(Pageable pageable) {
        return leaveRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Leave get(UUID id) {
        return leaveRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Leave", id));
    }

    @Transactional(readOnly = true)
    public List<LeaveType> leaveTypes() {
        return leaveTypeRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public LeaveType leaveType(UUID id) {
        return leaveTypeRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("LeaveType", id));
    }

    @Transactional
    public Leave save(Leave leave) {
        return leaveRepository.save(leave);
    }

    @Transactional
    public void updateStatus(UUID id, LeaveStatus status) {
        Leave leave = get(id);
        leave.setStatus(status);
        leaveRepository.save(leave);
    }

    @Transactional
    public void delete(UUID id) {
        leaveRepository.deleteById(id);
    }
}
