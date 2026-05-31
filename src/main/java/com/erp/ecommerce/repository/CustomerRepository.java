package com.erp.ecommerce.repository;

import com.erp.ecommerce.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @Query("SELECT c FROM Customer c WHERE :q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Customer> search(@Param("q") String q, Pageable pageable);

    Optional<Customer> findByEmailIgnoreCase(String email);
}
