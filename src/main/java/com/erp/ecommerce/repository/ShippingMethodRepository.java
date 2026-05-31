package com.erp.ecommerce.repository;

import com.erp.ecommerce.entity.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, UUID> {
}
