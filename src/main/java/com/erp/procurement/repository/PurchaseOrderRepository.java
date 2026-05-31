package com.erp.procurement.repository;

import com.erp.procurement.entity.PurchaseOrder;
import com.erp.procurement.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    @Query("SELECT p FROM PurchaseOrder p WHERE :q IS NULL OR LOWER(p.poNumber) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(p.vendor.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<PurchaseOrder> search(@Param("q") String q, Pageable pageable);

    List<PurchaseOrder> findByVendorOrderByOrderDateDesc(Vendor vendor);

    List<PurchaseOrder> findAllByOrderByOrderDateDesc();
}
