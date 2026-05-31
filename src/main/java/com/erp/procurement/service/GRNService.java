package com.erp.procurement.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.procurement.entity.GRN;
import com.erp.procurement.enums.GRNStatus;
import com.erp.procurement.enums.POStatus;
import com.erp.procurement.repository.GRNRepository;
import com.erp.procurement.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GRNService {

    private final GRNRepository grnRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional(readOnly = true)
    public Page<GRN> search(String q, Pageable pageable) {
        return grnRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public GRN get(UUID id) {
        return grnRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("GRN", id));
    }

    public GRN save(GRN grn) {
        if (grn.isNew()) {
            grn.setGrnNumber(nextGrnNumber());
        }
        if (grn.getReceivedDate() == null) {
            grn.setReceivedDate(LocalDate.now());
        }
        GRN saved = grnRepository.save(grn);
        // Reflect receipt status back onto the linked PO.
        if (saved.getPurchaseOrder() != null) {
            var po = saved.getPurchaseOrder();
            po.setStatus(saved.getStatus() == GRNStatus.PARTIAL ? POStatus.PARTIAL : POStatus.RECEIVED);
            purchaseOrderRepository.save(po);
        }
        return saved;
    }

    public void delete(UUID id) {
        grnRepository.deleteById(id);
    }

    private String nextGrnNumber() {
        long count = grnRepository.count();
        return String.format("GRN-%04d", count + 1);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
