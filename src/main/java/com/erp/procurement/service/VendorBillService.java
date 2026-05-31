package com.erp.procurement.service;

import com.erp.procurement.entity.Vendor;
import com.erp.procurement.entity.VendorBill;
import com.erp.procurement.repository.VendorBillRepository;
import com.erp.procurement.repository.VendorRepository;
import com.erp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorBillService {

    private final VendorBillRepository vendorBillRepository;
    private final VendorRepository vendorRepository;

    @Transactional(readOnly = true)
    public List<VendorBill> forVendor(UUID vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> ResourceNotFoundException.of("Vendor", vendorId));
        return vendorBillRepository.findByVendorOrderByDueDateAsc(vendor);
    }

    public VendorBill save(VendorBill bill) {
        if (bill.isNew()) {
            bill.setBillNumber(nextBillNumber());
        }
        return vendorBillRepository.save(bill);
    }

    private String nextBillNumber() {
        long count = vendorBillRepository.count();
        return String.format("BILL-%04d", count + 1);
    }
}
