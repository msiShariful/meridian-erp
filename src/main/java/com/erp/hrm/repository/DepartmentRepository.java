package com.erp.hrm.repository;

import com.erp.hrm.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    @Query("SELECT d FROM Department d WHERE :q IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(d.head) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Department> search(@Param("q") String q, Pageable pageable);
}
