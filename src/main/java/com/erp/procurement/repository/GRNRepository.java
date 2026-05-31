package com.erp.procurement.repository;

import com.erp.procurement.entity.GRN;
import com.erp.procurement.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GRNRepository extends JpaRepository<GRN, UUID> {
    @Query("SELECT g FROM GRN g WHERE :q IS NULL OR LOWER(g.grnNumber) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(g.purchaseOrder.poNumber) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(g.receivedBy) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<GRN> search(@Param("q") String q, Pageable pageable);

    List<GRN> findByPurchaseOrderOrderByReceivedDateDesc(PurchaseOrder purchaseOrder);
}
