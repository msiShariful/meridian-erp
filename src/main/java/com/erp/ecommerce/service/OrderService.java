package com.erp.ecommerce.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.ecommerce.entity.Order;
import com.erp.ecommerce.enums.OrderStatus;
import com.erp.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<Order> search(String q, OrderStatus status, Pageable pageable) {
        return orderRepository.search(blank(q), status, pageable);
    }

    @Transactional(readOnly = true)
    public Order get(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", id));
    }

    @Transactional(readOnly = true)
    public Order getByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderNumber));
    }

    @Transactional(readOnly = true)
    public List<Order> byCustomerEmail(String email) {
        if (email == null || email.isBlank()) return List.of();
        return orderRepository.findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(email.trim());
    }

    /** Generates the next sequential order number, e.g. "ORD-0001". */
    public String nextOrderNumber() {
        long count = orderRepository.count();
        return String.format("ORD-%04d", count + 1);
    }

    public Order save(Order order) {
        if (order.getOrderNumber() == null || order.getOrderNumber().isBlank()) {
            order.setOrderNumber(nextOrderNumber());
        }
        return orderRepository.save(order);
    }

    public void updateStatus(UUID id, OrderStatus status) {
        Order order = get(id);
        order.setStatus(status);
        orderRepository.save(order);
    }

    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
