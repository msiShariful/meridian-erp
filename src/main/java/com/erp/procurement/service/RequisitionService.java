package com.erp.procurement.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.procurement.entity.Requisition;
import com.erp.procurement.enums.RequisitionStatus;
import com.erp.procurement.repository.RequisitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;

    @Transactional(readOnly = true)
    public Page<Requisition> search(String q, Pageable pageable) {
        return requisitionRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public Requisition get(UUID id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Requisition", id));
    }

    public Requisition save(Requisition requisition) {
        if (requisition.isNew()) {
            requisition.setReqNumber(nextReqNumber());
        }
        return requisitionRepository.save(requisition);
    }

    public void updateStatus(UUID id, RequisitionStatus status) {
        Requisition req = get(id);
        req.setStatus(status);
        requisitionRepository.save(req);
    }

    public void delete(UUID id) {
        requisitionRepository.deleteById(id);
    }

    private String nextReqNumber() {
        long count = requisitionRepository.count();
        return String.format("REQ-%04d", count + 1);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
