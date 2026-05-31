package com.erp.hrm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.hrm.entity.Department;
import com.erp.hrm.entity.Designation;
import com.erp.hrm.repository.DepartmentRepository;
import com.erp.hrm.repository.DesignationRepository;
import com.erp.hrm.repository.EmployeeRepository;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public Page<Department> search(String q, Pageable pageable) {
        return departmentRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<Department> all() {
        return departmentRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public Department get(UUID id) {
        return departmentRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Department", id));
    }

    @Transactional(readOnly = true)
    public long employeeCount(UUID departmentId) {
        return employeeRepository.countByDepartmentId(departmentId);
    }

    @Transactional(readOnly = true)
    public List<Designation> designations() {
        return designationRepository.findAll(Sort.by("title"));
    }

    @Transactional(readOnly = true)
    public List<Designation> designationsByDepartment(UUID departmentId) {
        return designationRepository.findByDepartmentId(departmentId);
    }

    @Transactional
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Transactional
    public void delete(UUID id) {
        departmentRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
