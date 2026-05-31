package com.erp.procurement.repository;

import com.erp.procurement.entity.Vendor;
import com.erp.procurement.entity.VendorBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VendorBillRepository extends JpaRepository<VendorBill, UUID> {
    List<VendorBill> findByVendorOrderByDueDateAsc(Vendor vendor);
}
