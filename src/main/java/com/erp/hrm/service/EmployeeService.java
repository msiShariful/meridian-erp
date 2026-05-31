package com.erp.hrm.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.hrm.entity.Employee;
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
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public Page<Employee> search(String q, Pageable pageable) {
        return employeeRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public List<Employee> all() {
        return employeeRepository.findAll(Sort.by("firstName"));
    }

    @Transactional(readOnly = true)
    public Employee get(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Employee", id));
    }

    @Transactional
    public Employee save(Employee employee) {
        if (employee.getEmployeeId() == null || employee.getEmployeeId().isBlank()) {
            long next = employeeRepository.count() + 1;
            employee.setEmployeeId(String.format("EMP-%04d", next));
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public void delete(UUID id) {
        employeeRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
