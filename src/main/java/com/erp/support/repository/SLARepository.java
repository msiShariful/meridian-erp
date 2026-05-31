package com.erp.support.repository;

import com.erp.support.entity.SLA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SLARepository extends JpaRepository<SLA, UUID> {
}
