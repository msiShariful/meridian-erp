package com.erp.accounting.service;

import com.erp.accounting.entity.Payment;
import com.erp.accounting.repository.PaymentRepository;
import com.erp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Page<Payment> search(String q, Pageable pageable) {
        return paymentRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public Payment get(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Payment", id));
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void delete(UUID id) {
        paymentRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
