package com.erp.ecommerce.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.ecommerce.entity.Customer;
import com.erp.ecommerce.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public Page<Customer> search(String q, Pageable pageable) {
        return customerRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public Customer get(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Customer", id));
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void delete(UUID id) {
        customerRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
