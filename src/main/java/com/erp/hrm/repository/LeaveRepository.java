package com.erp.hrm.repository;

import com.erp.hrm.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeaveRepository extends JpaRepository<Leave, UUID> {
}
