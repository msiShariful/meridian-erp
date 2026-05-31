package com.erp.hrm.repository;

import com.erp.hrm.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    @Query("SELECT e FROM Employee e WHERE :q IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Employee> search(@Param("q") String q, Pageable pageable);

    long countByDepartmentId(UUID departmentId);
}
