package com.erp.hrm.repository;

import com.erp.hrm.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, UUID> {
}
