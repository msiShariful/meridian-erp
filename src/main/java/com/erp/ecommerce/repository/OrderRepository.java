package com.erp.ecommerce.repository;

import com.erp.ecommerce.entity.Order;
import com.erp.ecommerce.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o WHERE (:q IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:status IS NULL OR o.status = :status)")
    Page<Order> search(@Param("q") String q, @Param("status") OrderStatus status, Pageable pageable);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(String customerEmail);

    long countByStatus(OrderStatus status);
}
